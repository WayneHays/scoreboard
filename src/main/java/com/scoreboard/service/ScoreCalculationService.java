package com.scoreboard.service;

import com.scoreboard.model.OngoingMatch;
import com.scoreboard.model.entity.Player;

public class ScoreCalculationService {
    private static final int GAMES_TO_WIN_SET = 6;
    private static final int SETS_TO_WIN_MATCH = 2;
    private static final int TIEBREAK_POINTS_TO_WIN = 7;
    public static final int MAX_POSSIBLE_POINTS_PER_GAME = 40;
    public static final int ADVANTAGE_TO_WIN_TIEBREAK = 2;


    public void winPoint(OngoingMatch ongoingMatch, Player player) {
        if (ongoingMatch.getWinner() != null) {
            throw new IllegalArgumentException("Match is already finished");
        }

        if (ongoingMatch.isTieBreak()) {
            handleTieBreakPoint(ongoingMatch, player);
        } else {
            handleRegularPoint(ongoingMatch, player);
        }
    }

    private void handleTieBreakPoint(OngoingMatch ongoingMatch, Player player) {
        ongoingMatch.awardTieBreakPoint(player);

        Player opponent = getOpponent(ongoingMatch, player);
        int playerTieBreakPoints = ongoingMatch.getTieBreakPoints(player);
        int opponentTieBreakPoints = ongoingMatch.getTieBreakPoints(opponent);

        if (isTieBreakOver(playerTieBreakPoints, opponentTieBreakPoints)) {
            playerWonTieBreak(ongoingMatch, player);
        }
    }

    private Player getOpponent(OngoingMatch ongoingMatch, Player player) {
        return player.equals(ongoingMatch.getFirstPlayer())
                ? ongoingMatch.getSecondPlayer()
                : ongoingMatch.getFirstPlayer();
    }

    private boolean isTieBreakOver(int playerTieBreakPoints, int opponentTieBreakPoints) {
        return playerTieBreakPoints >= TIEBREAK_POINTS_TO_WIN
               && playerTieBreakPoints - opponentTieBreakPoints >= ADVANTAGE_TO_WIN_TIEBREAK;
    }

    private void playerWonTieBreak(OngoingMatch ongoingMatch, Player player) {
        ongoingMatch.setTieBreak(false);
        ongoingMatch.resetAllTieBreakPoints();
        ongoingMatch.awardGame(player);

        playerWonSet(ongoingMatch, player);
    }

    private void playerWonSet(OngoingMatch ongoingMatch, Player player) {
        ongoingMatch.resetAllGames();
        ongoingMatch.resetAllPoints();
        ongoingMatch.awardSet(player);

        if (ongoingMatch.getSets(player) == SETS_TO_WIN_MATCH) {
            ongoingMatch.setWinner(player);
        }
    }

    private void handleRegularPoint(OngoingMatch ongoingMatch, Player player) {
        Player opponent = getOpponent(ongoingMatch, player);
        int playerPoints = ongoingMatch.getPoints(player);
        int opponentPoints = ongoingMatch.getPoints(opponent);

        if (wasDeuce(playerPoints, opponentPoints)) {
            ongoingMatch.setAdvantage(player);
            return;
        }

        if (ongoingMatch.getAdvantage() != null) {
            if (ongoingMatch.getAdvantage().equals(player)) {
                playerWonGame(ongoingMatch, player);
            } else {
                ongoingMatch.setAdvantage(null);
            }
            return;
        }

        ongoingMatch.awardTennisPoint(player);

        if (ongoingMatch.getPoints(player) > MAX_POSSIBLE_POINTS_PER_GAME) {
            playerWonGame(ongoingMatch, player);
        }
    }

    private boolean wasDeuce(int playerPoints, int opponentPoints) {
        return playerPoints == MAX_POSSIBLE_POINTS_PER_GAME && opponentPoints == MAX_POSSIBLE_POINTS_PER_GAME;
    }

    private void playerWonGame(OngoingMatch ongoingMatch, Player player) {
        ongoingMatch.resetAllPoints();
        ongoingMatch.setAdvantage(null);
        ongoingMatch.awardGame(player);

        checkSetFinished(ongoingMatch, player);
    }

    private void checkSetFinished(OngoingMatch ongoingMatch, Player player) {
        Player opponent = getOpponent(ongoingMatch, player);
        int playerGames = ongoingMatch.getGames(player);
        int opponentGames = ongoingMatch.getGames(opponent);

        if (isTieBreak(playerGames, opponentGames)) {
            ongoingMatch.setTieBreak(true);
            return;
        }

        if (hasCurrentGameOver(playerGames, opponentGames)) {
            playerWonSet(ongoingMatch, player);
        }
    }

    private boolean hasCurrentGameOver(int playerGames, int opponentGames) {
        return playerGames >= GAMES_TO_WIN_SET && playerGames - opponentGames >= 2;
    }

    private boolean isTieBreak(int playerGames, int opponentGames) {
        return playerGames == GAMES_TO_WIN_SET && opponentGames == GAMES_TO_WIN_SET;
    }
}
