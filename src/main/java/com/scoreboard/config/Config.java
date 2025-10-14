package com.scoreboard.config;

public interface Config {
    String get(String key);
    int getInt(String key);
}
