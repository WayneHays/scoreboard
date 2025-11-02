package com.scoreboard.tennisrules.standard;

import com.scoreboard.tennisrules.MatchRules;

public class StandardMatchRules implements MatchRules {
    private static final int SETS_TO_WIN_MATCH = 2;

    @Override
    public int setsToWinMatch() {
        return SETS_TO_WIN_MATCH;
    }
}
