package com.scoreboard.tennisrules.standard;

import com.scoreboard.tennisrules.GameRules;

public class StandardGameRules implements GameRules {
    private static final boolean IS_ADVANTAGE_ENABLED = true;

    @Override
    public boolean isAdvantageEnabled() {
        return IS_ADVANTAGE_ENABLED;
    }
}
