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
            String msg = "{\"name\": \"Pi\", " +
                    "\"spark_home\" : \"/Users/laiqingquan/spark-2.0.0-bin-hadoop2.7\", " +
                    "\"main_class\": \"org.apache.spark.examples.SparkPi\"," +
                    " \"master\" : \"local\", " +
                    "\"deploy_mode\": \"client\", " +
                    "\"app_resource\" : \"/Users/laiqingquan/spark-2.0.0-bin-hadoop2.7/examples/jars/spark-examples_2.11-2.0.0.jar\""+
                    "} \t\n";
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


