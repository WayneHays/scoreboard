package com.scoreboard.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationConfig {
    public static final String INITIAL_PLAYERS_FILE = "/initial-players.txt";
    public static final String HIBERNATE_PROPERTIES_FILE = "hibernate.properties";
    public static final int PAGE_SIZE = 10;
}
