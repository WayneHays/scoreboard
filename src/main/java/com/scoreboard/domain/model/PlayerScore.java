package com.scoreboard.domain.model;

import lombok.Getter;

@Getter
public class PlayerScore {
    private Points points;
    private int games;
    private int sets;
    private int tieBreakPoints;

    PlayerScore() {
        this.points = Points.ZERO;
    }

    void addPoint() {
        this.points = points.next();
    }

    void addGame() {
        games++;
    }

    void addSet() {
        sets++;
    }

    void addTieBreakPoint() {
        tieBreakPoints++;
    }

    void resetPoints() {
        this.points = Points.ZERO;
    }

    void resetGames() {
        games = 0;
    }

    void resetTieBreakPoints() {
        tieBreakPoints = 0;
    }

    void resetToDeuce() {
        this.points = Points.FORTY;
    }
}
