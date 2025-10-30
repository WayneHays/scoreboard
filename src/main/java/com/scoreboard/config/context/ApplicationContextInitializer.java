package com.scoreboard.config.context;

import com.scoreboard.config.servicediscovery.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationContextInitializer {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationContextInitializer.class);

    public ApplicationContext initialize() {
        logger.info("Initializing ApplicationContext");

        ApplicationContext context = new ApplicationContext();
        ServiceDiscovery.discoverAndRegister(context);

        logger.info("ApplicationContext initialized with {} services", context.getServiceCount());
        return context;
    }
}
