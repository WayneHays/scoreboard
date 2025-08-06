package com.scoreboard.service;

import com.scoreboard.model.MatchWithScore;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;

public class ScoreCalculationService {
    private static final ScoreCalculationService INSTANCE = new ScoreCalculationService();
    private boolean isFinished;

    public static ScoreCalculationService getInstance() {
        return INSTANCE;
    }

    public boolean isMatchFinished() {
        return isFinished;
    }

    public MatchWithScore calculate(MatchWithScore matchWithScore, Player pointWinner) {
            Score score = matchWithScore.getScore();
        }

    }
}
