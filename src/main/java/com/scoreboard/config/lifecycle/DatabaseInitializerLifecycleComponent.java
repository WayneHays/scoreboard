package com.scoreboard.config.lifecycle;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.initialization.database.DatabaseInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseInitializerLifecycleComponent implements LifecycleComponent {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializerLifecycleComponent.class);

    @Override
    public void start(ApplicationContext context) {
        DatabaseInitializer initializer = context.get(DatabaseInitializer.class);
        initializer.initialize();
        logger.info("Database initialized with startup data");
    }

    @Override
    public void stop() {
        logger.debug("DatabaseInitializer stopped");
    }

    @Override
    public String getName() {
        return "DatabaseInitializer";
    }
}
