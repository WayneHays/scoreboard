package com.scoreboard.start_initialization;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.config.Config;
import com.scoreboard.config.ServiceProvider;
import com.scoreboard.start_initialization.data_source.DataSource;
import com.scoreboard.start_initialization.data_source.TextFileDataSource;

public class TextFileDataSourceFactory implements DataSourceFactory, ServiceProvider {
    private static final String PLAYERS_FILE = "files.initial.players";
    private final Config config;

    public TextFileDataSourceFactory() {
        this.config = ApplicationContext.get(Config.class);
    }

    @Override
    public DataSource create() {
        return new TextFileDataSource(config.get(PLAYERS_FILE));
    }
}
