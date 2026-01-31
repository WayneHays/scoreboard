package com.scoreboard.config.hibernate;

import com.scoreboard.config.properties.Config;
import com.scoreboard.exception.ApplicationStartupException;
import com.scoreboard.model.entity.Match;
import com.scoreboard.model.entity.Player;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HibernateUtil {
    private static volatile SessionFactory sessionFactory;
    private static volatile StandardServiceRegistry serviceRegistry;

    public static synchronized void initialize(Config config) {
        if (sessionFactory != null) {
            throw new IllegalStateException("HibernateUtil already initialized");
        }

        log.info("Initializing Hibernate SessionFactory");

        try {
            Properties properties = HibernateConfigManager.loadHibernateProperties(config);
            serviceRegistry = buildServiceRegistry(properties);
            sessionFactory = buildSessionFactory(serviceRegistry);

            log.info("Hibernate SessionFactory initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize Hibernate", e);
            shutdown();
            throw new ApplicationStartupException("Failed to initialize Hibernate", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException(
                    "HibernateUtil not initialized. Call initialize(Config) first.");
        }
        return sessionFactory;
    }

    public static synchronized void shutdown() {
        log.info("Shutting down Hibernate");

        closeSessionFactory();
        destroyServiceRegistry();

        log.info("Hibernate shutdown completed");
    }

    private static StandardServiceRegistry buildServiceRegistry(Properties properties) {
        try {
            return new StandardServiceRegistryBuilder()
                    .applySettings(properties)
                    .build();
        } catch (Exception e) {
            throw new ApplicationStartupException("Failed to build ServiceRegistry", e);
        }
    }

    private static SessionFactory buildSessionFactory(StandardServiceRegistry registry) {
        try {
            log.debug("Building Hibernate SessionFactory");

            MetadataSources sources = new MetadataSources(registry);
            addAnnotatedClasses(sources);

            Metadata metadata = sources.getMetadataBuilder().build();
            SessionFactory factory = metadata.getSessionFactoryBuilder().build();

            log.debug("SessionFactory built successfully");
            return factory;

        } catch (Exception e) {
            throw new ApplicationStartupException("Failed to build SessionFactory", e);
        }
    }

    private static void addAnnotatedClasses(MetadataSources sources) {
        sources.addAnnotatedClass(Player.class);
        sources.addAnnotatedClass(Match.class);
    }

    private static void closeSessionFactory() {
        if (sessionFactory != null) {
            try {
                sessionFactory.close();
                sessionFactory = null;
                log.debug("SessionFactory closed");
            } catch (Exception e) {
                log.error("Error closing SessionFactory", e);
            }
        }
    }

    private static void destroyServiceRegistry() {
        if (serviceRegistry != null) {
            try {
                StandardServiceRegistryBuilder.destroy(serviceRegistry);
                serviceRegistry = null;
                log.debug("ServiceRegistry destroyed");
            } catch (Exception e) {
                log.error("Error destroying ServiceRegistry", e);
            }
        }
    }
}
