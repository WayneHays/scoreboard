package com.scoreboard.util;

import com.scoreboard.exception.InitializeException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure()
                    .buildSessionFactory(new StandardServiceRegistryBuilder().build());
        } catch (Throwable throwable) {
            throw new InitializeException("Initial SessionFactory creation failed" + throwable);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }
}

