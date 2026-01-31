package com.scoreboard.config.properties;

import com.scoreboard.exception.ApplicationStartupException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PropertiesConfig implements Config {
    public static final String DEFAULT_PROPERTIES_FILE = "application.properties";

    private final Properties properties;
    private final String configFile;

    public PropertiesConfig() {
        this(DEFAULT_PROPERTIES_FILE);
    }

    public PropertiesConfig(String configFile) {
        this.configFile = configFile;
        log.debug("Loading configuration from: {}", configFile);

        this.properties = loadProperties();
        log.info("Configuration loaded successfully from: {}", configFile);
    }

    private Properties loadProperties() {
        Properties props = new Properties();

        try (InputStream input = PropertiesConfig.class
                .getClassLoader()
                .getResourceAsStream(configFile)) {

            if (input == null) {
                throw new ApplicationStartupException("Properties file not found");
            }

            props.load(input);

        } catch (IOException e) {
            throw new ApplicationStartupException("Failed to load configuration", e);
        }
        return props;
    }

    @Override
    public String get(String key, String defaultValue) {
        String value = properties.getProperty(key);

        if (value == null) {
            return defaultValue;
        }

        return value.trim();
    }

    @Override
    public int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);

        if (value == null) {
            return defaultValue;
        }

        return Integer.parseInt(value.trim());
    }
}
