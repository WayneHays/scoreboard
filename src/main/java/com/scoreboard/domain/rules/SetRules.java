package com.scoreboard.domain.rules;

import com.scoreboard.exception.TennisRulesValidationException;

public record SetRules(int gamesToWin) {
    private static final int GAMES_TO_WIN = 6;

    public SetRules {
        if (gamesToWin < GAMES_TO_WIN) {
            throw new TennisRulesValidationException("Games to win set must be > 6");
        }
    }

    public SetRules() {
        this(GAMES_TO_WIN);
    }
}
