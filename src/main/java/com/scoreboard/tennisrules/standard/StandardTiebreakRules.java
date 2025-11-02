package com.scoreboard.tennisrules.standard;

import com.scoreboard.tennisrules.TiebreakRules;

public class StandardTiebreakRules implements TiebreakRules {
    private static final int POINTS_TO_WIN_TIEBREAK = 7;
    private static final int MIN_TIEBREAK_POINTS_DIFFERENCE = 2;

    @Override
    public int pointsToWinTieBreak() {
        return POINTS_TO_WIN_TIEBREAK;
    }

    @Override
    public int minDifferenceToWinTieBreak() {
        return MIN_TIEBREAK_POINTS_DIFFERENCE;
    }
}
