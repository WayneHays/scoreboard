package com.scoreboard.domain.rules;

import com.scoreboard.exception.TennisRulesValidationException;

public record MatchRules(int setsToWin) {
    private static final int SETS_TO_WIN = 2;

    public MatchRules {
        if (setsToWin < SETS_TO_WIN) {
            throw new TennisRulesValidationException("Min sets to win match must be > 1, max sets to win match must be < 6");
        }
    }

    public MatchRules() {
        this(SETS_TO_WIN);
    }
}
