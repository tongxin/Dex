package cn.as.siat.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by laiqingquan on 16/9/19.
 */
public class TestAgentServer {
    public static void main(String args[]) {
        try {
            SocketChannel channel = SocketChannel.open();
            channel.connect(new InetSocketAddress("127.0.0.1", 8888));
            String msg = "{\"task\":\"cn.ac.siat.pi\", \"args\":[1,2,3]}\t\n";
            ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
            while(buffer.hasRemaining()) {
                channel.write(buffer);
            }
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
