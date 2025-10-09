package com.scoreboard.model;

import com.scoreboard.model.entity.Match;
import com.scoreboard.model.entity.Player;
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

    public Player getPlayerById(Long playerId) {
        if (match.getFirstPlayer().getId().equals(playerId)) {
            return match.getFirstPlayer();
        } else if (match.getSecondPlayer().getId().equals(playerId)) {
            return match.getSecondPlayer();
        }
        throw new IllegalArgumentException("Player with id " + playerId + " not found in this match");
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
}
