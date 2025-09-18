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

    public void awardTennisPoint(Player player) {
        int currentPoints = playersPoints.get(player);
        switch (currentPoints) {
            case 0 -> playersPoints.put(player, 15);
            case 15 -> playersPoints.put(player, 30);
            case 30 -> playersPoints.put(player, 40);
            default -> playersPoints.put(player, currentPoints + 1);
        }
    }

    public void awardTieBreakPoint(Player player) {
        playersTieBreakPoints.put(player, playersTieBreakPoints.get(player) + 1);
    }

    public void awardGame(Player player) {
        playersGames.put(player, playersGames.get(player) + 1);
    }

    public void awardSet(Player player) {
        playersSets.put(player, playersSets.get(player) + 1);
    }

    public void resetToDeuce() {
        playersPoints.replaceAll((player, points) -> 40);
    }

    public void resetAllPoints() {
        playersPoints.replaceAll((player, points) -> 0);
    }

    public void resetAllGames() {
        playersGames.replaceAll((player, games) -> 0);
    }

    public void resetAllTieBreakPoints() {
        playersTieBreakPoints.replaceAll((player, points) -> 0);
    }
}