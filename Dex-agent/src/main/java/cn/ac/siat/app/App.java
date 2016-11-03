package cn.ac.siat.app;

import cn.ac.siat.runnable.DefaultThreadPool;
import cn.ac.siat.runnable.GracefulThread;
import cn.ac.siat.servers.AgentServer;
import cn.ac.siat.servers.ShortMsgReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by laiqingquan on 16/9/19.
 */
public class App {

    private static Logger LOG = LoggerFactory.getLogger(App.class);
    public final static DefaultThreadPool POOL = new DefaultThreadPool<GracefulThread>();

    public static void main(String args[]){
        try {
            AgentServer server = new AgentServer(Configure.PORT);
            server.start();
            server.join();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        ShortMsgReceiver receiver =null;
//        try {
//            receiver = new ShortMsgReceiver("0.0.0.0", 10088);
//            System.out.println(receiver.receive(256, 100000).getAddress());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (receiver != null) {
//                receiver.close();
//            }
//        }
    }
}
