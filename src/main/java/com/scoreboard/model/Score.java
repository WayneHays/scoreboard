package com.scoreboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public class Score {
    private final Map<Player, Integer> playersPoints;
    private final Map<Player, Integer> playersGames;
    private final Map<Player, Integer> playersSets;
    private final Map<Player, Integer> playersTieBreakPoints;

    public Score() {
        this.playersTieBreakPoints = new HashMap<>();
        this.playersSets = new HashMap<>();
        this.playersGames = new HashMap<>();
        this.playersPoints = new HashMap<>();
    }

    public Score(Player player1, Player player2) {
        this();
        initializePlayerScore(player1);
        initializePlayerScore(player2);
    }

    private void initializePlayerScore(Player player) {
        playersPoints.put(player, 0);
        playersGames.put(player, 0);
        playersSets.put(player, 0);
        playersTieBreakPoints.put(player, 0);
    }

    public int getPoints(Player player) {
        return playersPoints.get(player);
    }

    public int getGames(Player player) {
        return playersGames.get(player);
    }

    public int getSets(Player player) {
        return playersSets.get(player);
    }

    public int getTieBreakPoints(Player player) {
        return playersTieBreakPoints.get(player);
    }

    public void setPoints(Player player, int points) {
        playersPoints.put(player, points);
    }

    public void setGames(Player player, int games) {
        playersGames.put(player, games);
    }

    public void setSets(Player player, int sets) {
        playersSets.put(player, sets);
    }

    public void setTieBreakPoints(Player player, int points) {
        playersTieBreakPoints.put(player, points);
    }

    public void resetPoints(Player firstPlayer, Player secondPlayer) {
        playersPoints.put(firstPlayer, 0);
        playersPoints.put(secondPlayer, 0);
    }

    public void resetGames(Player firstPlayer, Player secondPlayer) {
        playersGames.put(firstPlayer, 0);
        playersGames.put(secondPlayer, 0);
    }

    public void resetTieBreakPoints (Player firstPlayer, Player secondPlayer) {
        playersTieBreakPoints.put(firstPlayer, 0);
        playersTieBreakPoints.put(secondPlayer, 0);
    }
}