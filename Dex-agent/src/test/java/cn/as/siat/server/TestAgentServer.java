package cn.as.siat.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by laiqingquan on 16/9/19.
 */
public class TestAgentServer {
    public static void main(String args[]) {
        try {
            SocketChannel channel = SocketChannel.open();
            channel.connect(new InetSocketAddress("127.0.0.1", 8888));
            String msg = "{\"name\": \"Pi\", " +
                    "\"spark_home\" : \"/Users/laiqingquan/spark-2.0.1-bin-hadoop2.7\", " +
                    "\"main_class\": \"dex.core.DexServer\"," +
                    " \"master\" : \"local\", " +
                    "\"deploy_mode\": \"client\", " +
                    "\"app_resource\" : \"/Users/laiqingquan/Dex/Dex-server/target/scala-2.11/DexServer.jar\""+
                    "} \t\n";
            ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
            while(buffer.hasRemaining()) {
                channel.write(buffer);
            }

            CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
            buffer.clear();
            channel.read(buffer);
            buffer.flip();

            String tmp = decoder.decode(buffer.asReadOnlyBuffer()).toString();
            System.out.println(tmp);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


