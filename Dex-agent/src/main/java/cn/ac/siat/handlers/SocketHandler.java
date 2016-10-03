package cn.ac.siat.handlers;


import cn.ac.siat.launchers.SimpleLaunchers;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
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
        LOG.debug("{}", this.channel);
        ByteBuffer buffer = ByteBuffer.allocate(500);
        StringBuffer content = new StringBuffer();

        do {
            buffer.clear();

            try {
                channel.read(buffer);
                buffer.flip();

                CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
                String tmp = decoder.decode(buffer.asReadOnlyBuffer()).toString();
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

        try {
            LOG.debug("accept {} msg = {}", channel.getRemoteAddress(), content);

            JSONObject taskJson = JSON.parseObject(content.toString());
            String appName = taskJson.getString("name");
            String sparkHome = taskJson.getString("spark_home");
            String appResources = taskJson.getString("app_resource");
            String mainClass = taskJson.getString("main_class");
            String master = taskJson.getString("master");
            String deployMode = taskJson.getString("deploy_mode");
            SimpleLaunchers launcher = new SimpleLaunchers(appName, sparkHome, appResources, mainClass, master, deployMode);
            launcher.launch();

            Process process = launcher.launch();
            Scanner in = new Scanner(process.getInputStream());
            while (in.hasNextLine()) {
                String tmp = in.nextLine();

                if (tmp.startsWith("Pi")) {
                    System.out.println(tmp);
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


        try {
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
