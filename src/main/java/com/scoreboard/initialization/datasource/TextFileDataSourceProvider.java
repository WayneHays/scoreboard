package com.scoreboard.initialization.datasource;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.properties.Config;
import com.scoreboard.config.servicediscovery.ServiceProvider;
import com.scoreboard.constant.AppDefaults;
import com.scoreboard.constant.ConfigKeys;

public class TextFileDataSourceProvider implements ServiceProvider {


    @Override public Class<?> getServiceType() {
        return DataSource.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        Config config = context.get(Config.class);
        String fileName = config.get(
                ConfigKeys.INITIAL_PLAYERS_FILE,
                AppDefaults.DEFAULT_INITIAL_PLAYERS_FILE
        );

        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalStateException(
                    "Configuration property '" + ConfigKeys.INITIAL_PLAYERS_FILE + "' is missing or empty"
            );
        }

        return new TextFileDataSource(fileName);
    }
}
