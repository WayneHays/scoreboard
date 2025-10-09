package com.scoreboard.start_initialization;

import com.scoreboard.config.ConfigLoader;
import com.scoreboard.start_initialization.data_source.TextFileDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DataLoader.loadData(
                new TextFileDataSource(
                        ConfigLoader.get("files.initial.players")));
    }
}
