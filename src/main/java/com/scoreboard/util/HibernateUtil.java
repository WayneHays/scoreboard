package com.scoreboard.util;

import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class HibernateUtil {
    private static final ServiceRegistry SERVICE_REGISTRY;
    private static final SessionFactory SESSION_FACTORY;

    static {
        SERVICE_REGISTRY = configureServiceRegistry();
        SESSION_FACTORY = buildSessionFactory();
    }

    private HibernateUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        if (SESSION_FACTORY != null) {
            SESSION_FACTORY.close();
        }
        if (SERVICE_REGISTRY != null) {
            StandardServiceRegistryBuilder.destroy(SERVICE_REGISTRY);
        }
    }

    private static ServiceRegistry configureServiceRegistry() {
        try {
            Properties properties = loadHibernateProperties();
            return new StandardServiceRegistryBuilder().applySettings(properties).build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure ServiceRegistry", e);
        }
    }

    private static Properties loadHibernateProperties() {
        Properties properties = new Properties();
        try (InputStream stream = HibernateUtil.class.getClassLoader()
                .getResourceAsStream("hibernate.properties")) {
            if (stream == null) {
                throw new RuntimeException("hibernate.properties file not found");
            }
            properties.load(stream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load hibernate.properties", e);
        }
    }

    private static SessionFactory buildSessionFactory() {
        try {
            MetadataSources sources = new MetadataSources(SERVICE_REGISTRY);
            sources.addAnnotatedClass(Player.class);
            sources.addAnnotatedClass(Match.class);
            Metadata metadata = sources.getMetadataBuilder().build();
            return metadata.getSessionFactoryBuilder().build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Initializing Session Factory failed", e);
        }
    }
}

