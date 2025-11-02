package com.scoreboard.tennisrules.standard;

import com.scoreboard.tennisrules.SetRules;

public class StandardSetRules implements SetRules {
    private static final int GAMES_TO_WIN_SET = 6;
    private static final int MIN_GAMES_DIFFERENCE_TO_WIN_SET = 2;
    private static final boolean IS_TIEBREAK_ENABLED = true;

    @Override
    public int gamesToWinSet() {
        return GAMES_TO_WIN_SET;
    }

    @Override
    public int minGamesDifferenceToWinSet() {
        return MIN_GAMES_DIFFERENCE_TO_WIN_SET;
    }

    @Override
    public boolean isTiebreakEnabled() {
        return IS_TIEBREAK_ENABLED;
    }
}
