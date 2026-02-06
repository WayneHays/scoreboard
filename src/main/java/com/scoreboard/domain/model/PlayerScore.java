package com.scoreboard.domain.model;

import com.scoreboard.domain.model.state.Points;
import lombok.Getter;

@Getter
public class PlayerScore {
    private static final Points INITIAL_POINTS = Points.ZERO;
    private static final Points DEUCE_POINTS = Points.FORTY;

    private Points points;
    private int games;
    private int sets;
    private int tieBreakPoints;

    PlayerScore() {
        this.points = INITIAL_POINTS;
    }

    protected void addPoint() {
        this.points = points.next();
    }

    protected void addGame() {
        games++;
    }

    protected void addSet() {
        sets++;
    }

    protected void addTieBreakPoint() {
        tieBreakPoints++;
    }

    protected void resetPoints() {
        this.points = INITIAL_POINTS;
    }

    protected void resetGames() {
        games = 0;
    }

    protected void resetTieBreakPoints() {
        tieBreakPoints = 0;
    }

    protected void resetToDeuce() {
        this.points = DEUCE_POINTS;
    }
}
