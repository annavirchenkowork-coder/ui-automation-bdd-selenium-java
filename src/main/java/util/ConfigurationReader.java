package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class ConfigurationReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (FileInputStream file = new FileInputStream("src/test/resources/config.properties")) {
            PROPERTIES.load(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties file!", e);
        }
    }

    private ConfigurationReader() {}

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }
}