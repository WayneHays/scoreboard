package com.scoreboard.model.ongoingmatch;

import com.scoreboard.model.entity.Player;
import com.scoreboard.service.scorecalculation.Points;
import lombok.Getter;
import lombok.Setter;

public class OngoingMatch {
    @Getter
    private final Player player1;

    @Getter
    private final Player player2;

    @Getter
    @Setter
    private Player winner;

    @Getter
    @Setter
    private Player advantage;

    @Getter
    @Setter
    private boolean tieBreak;

    private final PlayerScore player1Score;
    private final PlayerScore player2Score;

    public OngoingMatch(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.player1Score = new PlayerScore();
        this.player2Score = new PlayerScore();
    }

    public boolean isFinished() {
        return winner != null;
    }

    public Player getPlayerByName(String playerName) {
        return player1.getName().equals(playerName) ? player1 : player2;
    }

    public Player getOpponent(Player player) {
        return player.equals(player1) ? player2 : player1;
    }

    public Points getPoints(Player player) {
        return getPlayerScore(player).getPoints();
    }

    public int getGames(Player player) {
        return getPlayerScore(player).getGames();
    }

    public int getSets(Player player) {
        return getPlayerScore(player).getSets();
    }

    public int getTieBreakPoints(Player player) {
        return getPlayerScore(player).getTieBreakPoints();
    }

    public void awardPointTo(Player scorer) {
        getPlayerScore(scorer).awardPoint();
    }

    public void awardGameTo(Player scorer) {
        getPlayerScore(scorer).awardGame();
        resetPointsForBoth();
        resetTieBreakPointsForBoth();
        setAdvantage(null);
    }

    public void awardSetTo(Player scorer) {
        getPlayerScore(scorer).awardSet();
        resetPointsForBoth();
        resetGamesForBoth();
        resetTieBreakPointsForBoth();
        setTieBreak(false);
        setAdvantage(null);
    }

    public void resetPointsToForty(Player player) {
        getPlayerScore(player).setPointsToForty();
    }

    public void awardTieBreakPointTo(Player scorer) {
        getPlayerScore(scorer).awardTieBreakPoint();
    }

    private void resetPointsForBoth() {
        player1Score.resetPoints();
        player2Score.resetPoints();
    }

    private void resetGamesForBoth() {
        player1Score.resetGames();
        player2Score.resetGames();
    }

    private void resetTieBreakPointsForBoth() {
        player1Score.resetTieBreakPoints();
        player2Score.resetTieBreakPoints();
    }

    private PlayerScore getPlayerScore(Player player) {
        return player.equals(player1) ? player1Score : player2Score;
    }
}
