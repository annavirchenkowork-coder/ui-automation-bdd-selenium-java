package util;

import java.io.InputStream;
import java.util.Properties;

public final class ConfigurationReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try (InputStream in = cl.getResourceAsStream("config.properties")) {
                if (in == null) {
                    throw new RuntimeException(
                            "config.properties not found on classpath. " +
                                    "Create it under src/test/resources or src/main/resources.");
                }
                PROPERTIES.load(in);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties file!", e);
        }
    }

    private ConfigurationReader() {}

    // value from -Dkey takes precedence, then file, else null/default
    public static String getProperty(String key) {
        String sys = System.getProperty(key);
        return (sys != null) ? sys : PROPERTIES.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        String sys = System.getProperty(key);
        return (sys != null) ? sys : PROPERTIES.getProperty(key, defaultValue);
    }
}