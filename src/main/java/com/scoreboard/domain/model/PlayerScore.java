package com.scoreboard.domain.model;

import com.scoreboard.domain.model.state.Points;
import lombok.Getter;

@Getter
public class PlayerScore {
    private static final Points INITIAL_POINTS = Points.ZERO;
    private static final Points DEUCE_POINTS = Points.FORTY;
    private static final int INITIAL_GAMES = 0;
    private static final int INITIAL_SETS = 0;
    private static final int INITIAL_TIE_BREAK_POINTS = 0;

    private Points points;
    private int games;
    private int sets;
    private int tieBreakPoints;

    PlayerScore() {
        this.points = INITIAL_POINTS;
        this.games = INITIAL_GAMES;
        this.sets = INITIAL_SETS;
        this.tieBreakPoints = INITIAL_TIE_BREAK_POINTS;
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
        games = INITIAL_GAMES;
    }

    protected void resetTieBreakPoints() {
        tieBreakPoints = INITIAL_TIE_BREAK_POINTS;
    }

    protected void resetToDeuce() {
        this.points = DEUCE_POINTS;
    }
}
