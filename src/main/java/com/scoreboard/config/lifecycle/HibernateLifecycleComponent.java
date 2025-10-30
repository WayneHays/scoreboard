package com.scoreboard.config.lifecycle;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.properties.Config;
import com.scoreboard.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateLifecycleComponent implements LifecycleComponent {
    private static final Logger logger = LoggerFactory.getLogger(HibernateLifecycleComponent.class);

    @Override
    public void start(ApplicationContext context) {
        Config config = context.get(Config.class);
        HibernateUtil.initialize(config);
        logger.info("Hibernate initialized");
    }

    @Override
    public void stop() {
        try {
            HibernateUtil.shutdown();
            logger.info("Hibernate shutdown completed");
        } catch (Exception e) {
            logger.error("Error during Hibernate shutdown", e);
        }
    }

    @Override
    public String getName() {
        return "Hibernate";
    }
}
