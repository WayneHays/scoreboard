package com.scoreboard.initialization.datasource;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.properties.Config;
import com.scoreboard.config.servicediscovery.ServiceProvider;

public class TextFileDataSourceProvider implements ServiceProvider {
    private static final String INITIAL_PLAYERS_FILE = "initial_players.txt";

    @Override public Class<?> getServiceType() {
        return DataSource.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        Config config = context.get(Config.class);
        String fileName = config.get(
                INITIAL_PLAYERS_FILE,
                INITIAL_PLAYERS_FILE
        );

        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalStateException(
                    "Configuration property '" + INITIAL_PLAYERS_FILE + "' is missing or empty"
            );
        }

        return new TextFileDataSource(fileName);
    }
}
