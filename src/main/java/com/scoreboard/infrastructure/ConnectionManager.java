package com.scoreboard.infrastructure;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;


public final class ConnectionManager {
    private static final String DATA_SOURCE_CLASS_NAME_KEY = "dataSourceClassName";
    private static final String DATA_SOURCE_URL = "dataSource.url";

    private static final HikariDataSource HIKARI_DATA_SOURCE;

    private ConnectionManager() {
    }

    static {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(PropertiesUtil.get(DATA_SOURCE_CLASS_NAME_KEY));
        config.setJdbcUrl(PropertiesUtil.get(DATA_SOURCE_URL));
        HIKARI_DATA_SOURCE = new HikariDataSource(config);
    }

    public static Connection open() throws SQLException {
        return HIKARI_DATA_SOURCE.getConnection();
    }
}
