package com.scoreboard.start_initialization;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.config.ServiceProvider;
import com.scoreboard.service.FinishedMatchPersistenceService;
import com.scoreboard.start_initialization.data_source.DataSource;

public class StartupDatabaseInitializerProvider implements ServiceProvider {

    @Override
    public Class<?> getServiceType() {
        return StartupDatabaseInitializer.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new StartupDatabaseInitializer(
                context.get(DataSource.class),
                context.get(FinishedMatchPersistenceService.class)
        );
    }
}
