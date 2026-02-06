package com.scoreboard.domain.rules;

public record GameRules(boolean isAdvantageEnabled) {
    private static final boolean DEFAULT_ADVANTAGE_STATUS = true;

    public GameRules() {
        this(DEFAULT_ADVANTAGE_STATUS);
    }
}
