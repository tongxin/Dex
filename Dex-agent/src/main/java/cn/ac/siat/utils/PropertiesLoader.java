package cn.ac.siat.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by laiqingquan on 16/10/3.
 */
public final class PropertiesLoader {
    public final static Properties load(String path) throws IOException {
        Properties properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream(path));
        return properties;
    }
}
