package com.scoreboard.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {
    private static final String PROPERTIES_FILE_NAME = "hibernate.properties";
    private static final String PROPERTIES_NOT_FOUND_MSG = "Properties file not found";

    private static final Properties PROPERTIES = new Properties();

    private PropertiesUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    static {
        loadProperties();
    }

    public static Properties getProperties() {
        Properties copy = new Properties();
        copy.putAll(PROPERTIES);
        return copy;
    }

    private static void loadProperties() {
        try (InputStream resourceAsStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            if (resourceAsStream == null) {
                throw new RuntimeException(PROPERTIES_NOT_FOUND_MSG);
            }
            PROPERTIES.load(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
