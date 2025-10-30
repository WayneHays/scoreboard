package com.scoreboard.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppDefaults {
    public static final int DEFAULT_MATCHES_PER_PAGE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 1;

    public static final String DEFAULT_INITIAL_PLAYERS_FILE = "initial_players.txt";
    public static final String HIBERNATE_CONFIG = "hibernate.properties";
}
