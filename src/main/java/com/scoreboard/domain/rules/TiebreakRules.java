package com.scoreboard.domain.rules;

import com.scoreboard.exception.TennisRulesValidationException;

public record TiebreakRules(int pointsToWin) {
    private static final int POINTS_TO_WIN = 6;

   public TiebreakRules {
       if (pointsToWin < POINTS_TO_WIN) {
           throw new TennisRulesValidationException("Points to win tiebreak must be > 6");
       }
   }

    public TiebreakRules() {
       this(POINTS_TO_WIN);
    }
}
