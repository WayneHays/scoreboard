package com.scoreboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public class Score {
    private static final int ZERO = 0;
    private static final int FIFTEEN = 15;
    private static final int THIRTY = 30;
    private static final int FORTY = 40;

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
        playersPoints.put(player, ZERO);
        playersGames.put(player, ZERO);
        playersSets.put(player, ZERO);
        playersTieBreakPoints.put(player, ZERO);
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

    public void awardTennisPoint(Player player) {
        int currentPoints = playersPoints.get(player);
        switch (currentPoints) {
            case ZERO -> playersPoints.put(player, FIFTEEN);
            case FIFTEEN -> playersPoints.put(player, THIRTY);
            case THIRTY -> playersPoints.put(player, FORTY);
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
        playersPoints.replaceAll((player, points) -> FORTY);
    }

    public void resetAllPoints() {
        playersPoints.replaceAll((player, points) -> ZERO);
    }

    public void resetAllGames() {
        playersGames.replaceAll((player, games) -> ZERO);
    }

    public void resetAllTieBreakPoints() {
        playersTieBreakPoints.replaceAll((player, points) -> ZERO);
    }
}