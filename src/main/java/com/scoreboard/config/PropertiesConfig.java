package com.scoreboard.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfig implements Config, ServiceProvider{
    private final Properties properties;

    public PropertiesConfig() {
        this.properties = loadProperties();
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = PropertiesConfig.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new RuntimeException("Properties file not found");
            }

            props.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
        return props;
    }

    @Override
    public String get(String key) {
        return properties.getProperty(key);  // ← исправлено: properties, а не PROPERTIES
    }

    @Override
    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));  // ← исправлено: properties
    }
}
