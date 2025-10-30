package com.scoreboard.config.properties;

public interface Config {
    String get(String key, String defaultValue);
    int getInt(String key, int defaultValue);
}
