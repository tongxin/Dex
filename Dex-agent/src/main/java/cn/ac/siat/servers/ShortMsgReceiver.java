package cn.ac.siat.servers;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by laiqingquan on 16/11/2.
 */
public class ShortMsgReceiver {
    private String host;
    private int port;
    private DatagramSocket socket;

    public ShortMsgReceiver(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
    }

    public DatagramPacket receive(int msgSize, int timeout) throws IOException {
        byte[] buffer = new byte[msgSize];
        DatagramPacket packet = new DatagramPacket(buffer, msgSize);

        socket = new DatagramSocket(new InetSocketAddress(this.host, this.port));
        socket.setSoTimeout(timeout);
        socket.receive(packet);
        socket.close();

        return packet;
    }

    public void close() {
        if (socket != null) {
            socket.close();
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
