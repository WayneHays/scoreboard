package com.scoreboard.config;

import com.scoreboard.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateInitializer implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(HibernateInitializer.class);

    @Override
    public Class<?> getServiceType() {
        return HibernateInitializer.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        logger.info("Initializing Hibernate...");

        Config config = context.get(Config.class);
        logger.debug("Retrieved configuration for Hibernate initialization");

        HibernateUtil.initialize(config);
        logger.info("Hibernate initialized successfully");

        return this;
    }
}
