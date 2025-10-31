package com.scoreboard.model.domain;

import com.scoreboard.service.scorecalculation.Points;
import lombok.Getter;

@Getter
public class PlayerScore {
    private Points points;
    private int games;
    private int sets;
    private int tieBreakPoints;


    public PlayerScore() {
        this.points = Points.ZERO;
    }

    public void awardPoint() {
        this.points = points.next();
    }

    void awardGame() {
        games++;
    }

    void awardSet() {
        sets++;
    }

    void awardTieBreakPoint() {
        tieBreakPoints++;
    }

    public void setPointsToForty() {
        this.points = Points.FORTY;
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
}
