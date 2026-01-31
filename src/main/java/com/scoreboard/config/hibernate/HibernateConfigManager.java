package com.scoreboard.config.hibernate;

import com.scoreboard.config.properties.Config;
import com.scoreboard.constant.AppDefaults;
import com.scoreboard.exception.ApplicationStartupException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HibernateConfigManager {
    private static final String HIBERNATE_CONFIG = "hibernate.properties";

    public static Properties loadHibernateProperties(Config config) {
        String configFile = config.get(
                HIBERNATE_CONFIG,
                HIBERNATE_CONFIG
        );

        log.debug("Loading Hibernate configuration from: {}", configFile);

        try (InputStream stream = getConfigStream(configFile)) {
            Properties properties = new Properties();
            properties.load(stream);

            log.debug("Loaded {} Hibernate properties", properties.size());
            return properties;

        } catch (IOException e) {
            throw new ApplicationStartupException(
                    "Failed to load Hibernate configuration from: " + configFile, e);
        }
    }

    private static InputStream getConfigStream(String configFile) {
        InputStream stream = HibernateConfigManager.class
                .getClassLoader()
                .getResourceAsStream(configFile);

        if (stream == null) {
            throw new ApplicationStartupException(
                    "Hibernate configuration file not found: " + configFile);
        }

        return stream;
    }
}
