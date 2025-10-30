package com.scoreboard.initialization.database;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.servicediscovery.ServiceProvider;
import com.scoreboard.service.finishedmatchpersistenceservice.FinishedMatchPersistenceService;
import com.scoreboard.initialization.factory.TestMatchFactory;
import com.scoreboard.initialization.datasource.DataSource;

public class DatabaseInitializerProvider implements ServiceProvider {

    @Override
    public Class<?> getServiceType() {
        return DatabaseInitializer.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new DatabaseInitializerImpl(
                context.get(DataSource.class),
                context.get(FinishedMatchPersistenceService.class),
                context.get(TestMatchFactory.class)
        );
    }
}
