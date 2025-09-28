package com.scoreboard.model;

import com.scoreboard.model.entity.Player;

import java.util.Map;

public class Score {
    private final Map<Player, PlayerScore> playersScores;

    public Score(Player player1, Player player2) {
        this.playersScores = Map.of(
                player1, new PlayerScore(),
                player2, new PlayerScore()
        );
    }

    public int getPoints(Player player) {
        return playersScores.get(player).getPoints();
    }

    public int getGames(Player player) {
        return playersScores.get(player).getGames();
    }

    public int getSets(Player player) {
        return playersScores.get(player).getSets();
    }

    public int getTieBreakPoints(Player player) {
        return playersScores.get(player).getTieBreakPoints();
    }

    public void awardTennisPoint(Player player) {
        playersScores.get(player).awardPoint();
    }

    public void awardTieBreakPoint(Player player) {
        playersScores.get(player).awardTieBreakPoint();
    }

    public void awardGame(Player player) {
        playersScores.get(player).awardGame();
    }

    public void awardSet(Player player) {
        playersScores.get(player).awardSet();
    }

    public void resetToDeuce() {
        playersScores.values().forEach(PlayerScore::resetToDeuce);
    }

    public void resetAllPoints() {
        playersScores.values().forEach(PlayerScore::resetPoints);
    }

    public void resetAllGames() {
        playersScores.values().forEach(PlayerScore::resetGames);
    }

    public void resetAllTieBreakPoints() {
        playersScores.values().forEach(PlayerScore::resetTieBreakPoints);
    }
}