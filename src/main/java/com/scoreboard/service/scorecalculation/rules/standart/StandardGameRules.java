package com.scoreboard.service.scorecalculation.rules.standart;

import com.scoreboard.service.scorecalculation.rules.GameRules;

public class StandardGameRules implements GameRules {
    private static final boolean IS_ADVANTAGE_ENABLED = true;

    @Override
    public boolean isAdvantageEnabled() {
        return IS_ADVANTAGE_ENABLED;
    }
}
