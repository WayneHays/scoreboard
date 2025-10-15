package com.scoreboard.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfig implements Config {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfig.class);
    private static final String DEFAULT_CONFIG_FILE = "application.properties";

    private final Properties properties;
    private final String configFile;

    public PropertiesConfig() {
        this(DEFAULT_CONFIG_FILE);
    }

    public PropertiesConfig(String configFile) {
        this.configFile = configFile;
        logger.debug("Loading configuration from: {}", configFile);

        this.properties = loadProperties();
        logger.info("Configuration loaded successfully from: {}", configFile);
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = PropertiesConfig.class
                .getClassLoader()
                .getResourceAsStream(configFile)) {

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
        String value = properties.getProperty(key);

        if (value == null) {
            logger.warn("Configuration property not found: {}", key);
            throw new IllegalArgumentException("Configuration property not found: " + key);
        }

        return value.trim();
    }

    @Override
    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}
