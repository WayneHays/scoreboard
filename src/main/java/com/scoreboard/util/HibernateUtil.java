package com.scoreboard.util;

import com.scoreboard.exception.ApplicationStartupException;
import com.scoreboard.entity.Match;
import com.scoreboard.entity.Player;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HibernateUtil {
    private static final String MSG_SESSION_FACTORY_CREATED = "SessionFactory creation failed: ";

    @Getter
    private static final SessionFactory sessionFactory;


    static {
        try {
            Configuration configuration = new Configuration()
                    .configure()
                    .addAnnotatedClass(Match.class)
                    .addAnnotatedClass(Player.class);
            sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            String message = MSG_SESSION_FACTORY_CREATED + e.getMessage();
            log.error(message, e);
            throw new ApplicationStartupException(message, e);
        }
    }
}
