package com.scoreboard.util;

import com.scoreboard.config.Config;
import com.scoreboard.model.entity.Match;
import com.scoreboard.model.entity.Player;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HibernateUtil {
    private static volatile SessionFactory sessionFactory;
    private static volatile ServiceRegistry serviceRegistry;

    public static synchronized void initialize(Config config) {
        if (sessionFactory != null) {
            throw new IllegalStateException("HibernateUtil already initialized");
        }

        try {
            serviceRegistry = configureServiceRegistry(config);
            sessionFactory = buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Hibernate", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException(
                    "HibernateUtil not initialized. Call initialize(Config) first."
            );
        }
        return sessionFactory;
    }

    public static synchronized void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
        if (serviceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
            serviceRegistry = null;
        }
    }

    private static ServiceRegistry configureServiceRegistry(Config config) {
        try {
            Properties properties = loadHibernateProperties(config);
            return new StandardServiceRegistryBuilder()
                    .applySettings(properties)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure ServiceRegistry", e);
        }
    }

    private static Properties loadHibernateProperties(Config config) {
        String configFile = config.get("hibernate.config");

        Properties properties = new Properties();
        try (InputStream stream = HibernateUtil.class.getClassLoader()
                .getResourceAsStream(configFile)) {

            if (stream == null) {
                throw new IllegalStateException(configFile + " file not found");
            }

            properties.load(stream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + configFile, e);
        }
    }

    private static SessionFactory buildSessionFactory() {
        try {
            MetadataSources sources = new MetadataSources(serviceRegistry);
            sources.addAnnotatedClass(Player.class);
            sources.addAnnotatedClass(Match.class);
            Metadata metadata = sources.getMetadataBuilder().build();
            return metadata.getSessionFactoryBuilder().build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to build SessionFactory", e);
        }
    }
}
