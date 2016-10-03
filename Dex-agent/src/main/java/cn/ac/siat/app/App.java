package cn.ac.siat.app;

import cn.ac.siat.runnable.DefaultThreadPool;
import cn.ac.siat.runnable.GracefulThread;
import cn.ac.siat.servers.AgentServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
    }
}
