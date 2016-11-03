package cn.ac.siat.handlers;


import cn.ac.siat.app.Configure;
import cn.ac.siat.launchers.SimpleLaunchers;
import cn.ac.siat.servers.ShortMsgReceiver;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Int;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Random;
import java.util.Scanner;

/**
 * 处理来自PG端的提交请求,处理流程:
 * 1.解析提交请求;
 * 2.使用launcher向Spark启动DexServer;
 * 3.打开UDP端口,等待DexServer返回网络信息;
 * 4.向PG端返回DexServer的网络信息;
 * 5.关闭与PG端的连接;
 *
 * Created by laiqingquan on 16/9/19.
 */
public class SocketHandler implements Runnable {
    private static Logger LOG = LoggerFactory.getLogger(SocketHandler.class);

    private SocketChannel channel;

    public SocketHandler(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        ByteBuffer request_buffer = ByteBuffer.allocate(500);
        StringBuffer content = new StringBuffer();

        //读取和解析请求信息
        do {
            request_buffer.clear();
            try {
                channel.read(request_buffer);
                request_buffer.flip();

                CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
                String tmp = decoder.decode(request_buffer.asReadOnlyBuffer()).toString();
                content.append(tmp);
                if ((content.charAt(content.length() - 1) == '\n') && (content.charAt(content.length() - 2) == '\t')) {
                    content.deleteCharAt(content.length() - 1);
                    content.deleteCharAt(content.length() - 1);
                    break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } while (true);

        //启动DexServer Application
        try {
            LOG.info("accept {}, msg = {}, launching spark application.", channel.getRemoteAddress(), content);

            JSONObject taskJson = JSON.parseObject(content.toString());
            String appName = taskJson.getString("name");
            String sparkHome = taskJson.getString("spark_home");
            String appResources = taskJson.getString("app_resource");
            String mainClass = taskJson.getString("main_class");
            String master = taskJson.getString("master");
            String deployMode = taskJson.getString("deploy_mode");
            SimpleLaunchers launcher = new SimpleLaunchers(appName, sparkHome, appResources, mainClass, master, deployMode);
            launcher.launch(Configure.HOST, String.valueOf(Configure.DRIVER_INFO_PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //打开UDP端口,等待DexServer返回网络信息
        try {
            LOG.info("Wait for the driver network info on {}", Configure.DRIVER_INFO_PORT);

            ShortMsgReceiver driverInfoReceiver = new ShortMsgReceiver("0.0.0.0", Configure.DRIVER_INFO_PORT);
            DatagramPacket driver_info_packet = driverInfoReceiver.receive(Configure.DRIVER_INFO_SIZE, Configure.DRIVER_TIMEOUT);
            JSONObject response_json = JSON.parseObject((new String(driver_info_packet.getData())).trim());
            response_json.put("host", driver_info_packet.getAddress().toString().substring(1));
            ByteBuffer response_buffer = ByteBuffer.wrap((response_json.toJSONString() + " \t\n").getBytes());
            this.channel.write(response_buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //关闭与PG端的连接
        try {
            LOG.info("Closing PG submit socket {}", channel.getRemoteAddress());

            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
