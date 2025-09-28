package com.scoreboard.startup;

import com.scoreboard.config.ApplicationConfig;
import com.scoreboard.startup.data_source.TextFileDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DataLoader.loadData(new TextFileDataSource(
                ApplicationConfig.INITIAL_PLAYERS_FILE));
    }
}
