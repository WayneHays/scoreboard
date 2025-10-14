package com.scoreboard.start_initialization.data_source;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.config.Config;
import com.scoreboard.config.ServiceProvider;

public class TextFileDataSourceProvider implements ServiceProvider {

    @Override public Class<?> getServiceType() {
        return DataSource.class; }

    @Override public Object createService(ApplicationContext context) {
        Config config = context.get(Config.class);
        String fileName = config.get("files.initial.players");
        return new TextFileDataSource(fileName);
    }
}
