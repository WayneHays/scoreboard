package com.scoreboard.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigKeys {
    public static final String MATCHES_PER_PAGE = "matches.per.page";
    public static final String INITIAL_PLAYERS_FILE = "files.initial.players";
    public static final String HIBERNATE_CONFIG_FILE = "hibernate.config";
}
