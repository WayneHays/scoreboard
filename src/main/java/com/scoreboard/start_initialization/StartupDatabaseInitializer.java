package com.scoreboard.start_initialization;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.config.ServiceProvider;
import com.scoreboard.service.FinishedMatchPersistenceService;
import com.scoreboard.start_initialization.data_source.DataSource;

public class StartupDatabaseInitializer implements DatabaseInitializer, ServiceProvider {
    private final DataSourceFactory dataSourceFactory;
    private final FinishedMatchPersistenceService persistenceService;

    public StartupDatabaseInitializer() {
        this.dataSourceFactory = ApplicationContext.get(DataSourceFactory.class);
        this.persistenceService = ApplicationContext.get(FinishedMatchPersistenceService.class);
    }

    @Override
    public void initialize() {
        DataSource dataSource = dataSourceFactory.create();
        StartupDataCreator dataCreator = new StartupDataCreator(persistenceService);
        dataCreator.createInitialData(dataSource);
    }
}
