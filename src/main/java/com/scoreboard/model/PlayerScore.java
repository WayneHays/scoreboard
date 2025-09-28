package com.scoreboard.model;

import lombok.Getter;

@Getter
public class PlayerScore {
    private static final int ZERO = 0;
    private static final int FIFTEEN = 15;
    private static final int THIRTY = 30;
    private static final int FORTY = 40;

    private int points = ZERO;
    private int games = ZERO;
    private int sets = ZERO;
    private int tieBreakPoints = ZERO;

    public void awardPoint() {
        switch (points) {
            case ZERO -> points = FIFTEEN;
            case FIFTEEN -> points = THIRTY;
            case THIRTY -> points = FORTY;
            default -> points += 1;
        }
    }

    public void awardGame() {
        games += 1;
    }

    public void awardSet() {
        sets += 1;
    }

    public void awardTieBreakPoint() {
        tieBreakPoints += 1;
    }

    public void resetPoints() {
        points = 0;
    }

    public void resetGames() {
        games = 0;
    }

    public void resetTieBreakPoints() {
        tieBreakPoints = 0;
    }

    public void resetToDeuce() {
        points = FORTY;
    }
}
