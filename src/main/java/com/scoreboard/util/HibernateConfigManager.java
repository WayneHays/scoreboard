package com.scoreboard.util;

import com.scoreboard.config.properties.Config;
import com.scoreboard.constant.AppDefaults;
import com.scoreboard.constant.ConfigKeys;
import com.scoreboard.exception.ApplicationStartupException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HibernateConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(HibernateConfigManager.class);

    public static Properties loadHibernateProperties(Config config) {
        String configFile = config.get(
                ConfigKeys.HIBERNATE_CONFIG_FILE,
                AppDefaults.HIBERNATE_CONFIG
        );

        logger.debug("Loading Hibernate configuration from: {}", configFile);

        try (InputStream stream = getConfigStream(configFile)) {
            Properties properties = new Properties();
            properties.load(stream);

            logger.debug("Loaded {} Hibernate properties", properties.size());
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
