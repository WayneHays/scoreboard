package com.scoreboard.service.scorecalculation.rules.standart;

import com.scoreboard.service.scorecalculation.rules.MatchRules;

public class StandardMatchRules implements MatchRules {
    private static final int SETS_TO_WIN_MATCH = 2;

    @Override
    public int setsToWinMatch() {
        return SETS_TO_WIN_MATCH;
    }
}
