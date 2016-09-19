package cn.ac.siat.servers;

import cn.ac.siat.handlers.SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by laiqingquan on 16/9/19.
 */
public class AgentServer extends Thread {
    private static Logger LOG = LoggerFactory.getLogger(AgentServer.class);

    private int port;

    private volatile boolean isShutdown;

    private ServerSocket serverSocket;

    public AgentServer(int port) throws IOException {
        this.port = port;

    }

    public void run() {
        isShutdown = false;
        ServerSocket serverSocket = null;

        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(this.port));
            serverSocketChannel.configureBlocking(true);
            LOG.info("Agent Server has been started up at threadID = {}!", getId());

            while (!isShutdown) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if(socketChannel != null){
                    LOG.info("host {} submit a request!", socketChannel.getRemoteAddress());
                    SocketHandler handler = new SocketHandler(socketChannel);
                    new Thread(handler).start();
                }
            }

            serverSocketChannel.close();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            isShutdown = true;

        }

    }

    public void shutdown() {
        isShutdown = true;
    }

}
