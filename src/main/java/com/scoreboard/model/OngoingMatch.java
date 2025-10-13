package com.scoreboard.model;

import com.scoreboard.model.entity.Match;
import com.scoreboard.model.entity.Player;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;

@AllArgsConstructor
public class OngoingMatch {
    private final Match match;
    private final Score score;

    public void withMatch(Consumer<Match> operation) {
        operation.accept(match);
    }

    public Player getPlayer(String name) {
        if (match.getFirstPlayer().getName().equals(name)) {
            return match.getFirstPlayer();
        }

        if (match.getSecondPlayer().getName().equals(name)) {
            return match.getSecondPlayer();
        }

        throw new IllegalArgumentException(
                "Player with name " + name + " is not part of this match"
        );
    }

    public Player getOpponent(Player player) {
        if (player.equals(getFirstPlayer())) {
            return getSecondPlayer();
        }

        if (player.equals(getSecondPlayer())) {
            return getFirstPlayer();
        }

        throw new IllegalArgumentException("Player is not part of this match");
    }

    public void winGame(Player player) {
        resetAllPoints();
        resetAdvantage();
        awardGame(player);
    }

    public void winTieBreak(Player player) {
        setTieBreak(false);
        resetAllTieBreakPoints();
        awardGame(player);
    }

    public void winSet(Player player) {
        resetAllGames();
        resetAllPoints();
        resetAdvantage();
        awardSet(player);
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

    public Player getAdvantageStatus() {
        return score.getAdvantage();
    }

    public int getTieBreakPoints(Player player) {
        return score.getTieBreakPoints(player);
    }

    public boolean isTieBreak() {
        return score.isTieBreak();
    }

    public Player getFirstPlayer() {
        return match.getFirstPlayer();
    }

    public Player getSecondPlayer() {
        return match.getSecondPlayer();
    }

    public Player getWinner() {
        return match.getWinner();
    }

    public void setWinner(Player player) {
        match.setWinner(player);
    }

    public void setTieBreak(boolean isTieBreak) {
        score.setTieBreak(isTieBreak);
    }

    public void setAdvantage(Player player) {
        score.setAdvantage(player);
    }

    public void awardTennisPoint(Player player) {
        score.awardTennisPoint(player);
    }

    public void awardTieBreakPoint(Player player) {
        score.awardTieBreakPoint(player);
    }

    public void awardGame(Player player) {
        score.awardGame(player);
    }

    public void awardSet(Player player) {
        score.awardSet(player);
    }

    public void resetAdvantage() {
        score.setAdvantage(null);
    }

    public void resetAllGames() {
        score.resetAllGames();
    }

    private void resetAllPoints() {
        score.resetAllPoints();
    }

    private void resetAllTieBreakPoints() {
        score.resetAllTieBreakPoints();
    }
}
