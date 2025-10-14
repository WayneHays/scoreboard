package com.scoreboard.start_initialization;

import com.scoreboard.start_initialization.data_source.DataSource;

public interface DataSourceFactory {
    DataSource create();
}
