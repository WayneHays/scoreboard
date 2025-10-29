package com.scoreboard.service.scorecalculation.rules;

public interface SetRules {
    int gamesToWinSet();
    int minGamesDifferenceToWinSet();
    boolean isTiebreakEnabled();
}
