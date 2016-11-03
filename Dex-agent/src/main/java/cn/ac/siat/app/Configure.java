package cn.ac.siat.app;

import cn.ac.siat.utils.PropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by laiqingquan on 16/10/3.
 */
public class Configure {
    private static Logger LOG = LoggerFactory.getLogger(Configure.class);

    public volatile static String HOST;
    public volatile static int PORT;
    public volatile static int DRIVER_INFO_PORT;
    public volatile static int DRIVER_INFO_SIZE;
    public volatile static int DRIVER_TIMEOUT;

    static {
        try {
            Properties properties = PropertiesLoader.load("base.properties");
            HOST = properties.getProperty("Agent.server.host");
            PORT = Integer.parseInt(properties.getProperty("Agent.server.port"));
            DRIVER_INFO_PORT = Integer.parseInt(properties.getProperty("Agent.server.driver_info.port"));
            DRIVER_INFO_SIZE = Integer.parseInt(properties.getProperty("Agent.server.driver_info.size"));
            DRIVER_TIMEOUT = Integer.parseInt(properties.getProperty("Agent.server.driver_info.timeout"));

            LOG.info("HOST : {}", HOST);
            LOG.info("PORT : {}", PORT);
            LOG.info("DRIVER_INFO_PORT : {}", DRIVER_INFO_PORT);
            LOG.info("DRIVER_INFO_SIZE : {}", DRIVER_INFO_SIZE);
            LOG.info("DRIVER_TIMEOUT : {}", DRIVER_TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
