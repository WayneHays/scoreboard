package com.scoreboard.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class OngoingMatch {
    @Getter
    private final Match match;

    @Getter
    private final UUID uuid;
    private final Score score;

    @Getter
    @Setter
    private boolean isTieBreak;

    @Getter
    @Setter
    private Player advantage;

    public OngoingMatch(Match match, Score score, UUID uuid) {
        this.match = match;
        this.uuid = uuid;
        this.score = score;
        this.isTieBreak = false;
        this.advantage = null;
    }


    public Player getFirstPlayer() {
        return match.getFirstPlayer();
    }

    public Player getSecondPlayer() {
        return match.getSecondPlayer();
    }

    public int getSets(Player player) {
        return score.getSets(player);
    }

    public int getGames(Player player) {
        return score.getGames(player);
    }

    public int getPoints(Player player) {
        return score.getPoints(player);
    }

    public int getTieBreakPoints(Player player) {
        return score.getTieBreakPoints(player);
    }

    public boolean isWinner(Player player) {
        return match.getWinner() != null && match.getWinner().equals(player);
    }

    public Player getWinner() {
        return match.getWinner();
    }

    public void setWinner(Player player) {
        match.setWinner(player);
    }

    public void awardTennisPoint(Player player) {
        score.awardTennisPoint(player);
    }

    public void awardGame(Player player) {
        score.awardGame(player);
    }

    public void awardSet(Player player) {
        score.awardSet(player);
    }

    public void resetAllPoints() {
        score.resetAllPoints();
    }

    public void resetAllGames() {
        score.resetAllGames();
    }

    public void resetAllTieBreakPoints() {
        score.resetAllTieBreakPoints();
    }

    public void awardTieBreakPoint(Player player) {
        score.awardTieBreakPoint(player);
    }

    public void resetToDeuce() {
        score.resetToDeuce();
    }
}
