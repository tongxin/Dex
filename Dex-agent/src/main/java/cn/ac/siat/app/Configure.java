package cn.ac.siat.app;

import cn.ac.siat.utils.PropertiesLoader;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by laiqingquan on 16/10/3.
 */
public class Configure {
    public volatile static String HOST;
    public volatile static int PORT;

    static {
        try {
            Properties properties = PropertiesLoader.load("base.properties");
            HOST = properties.getProperty("Agent.server.host");
            PORT = Integer.parseInt(properties.getProperty("Agent.server.port"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
