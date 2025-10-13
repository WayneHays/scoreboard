package com.scoreboard.service;

import com.scoreboard.model.OngoingMatch;
import com.scoreboard.model.entity.Player;

public class ScoreCalculationService {
    private static final int GAMES_TO_WIN_SET = 6;
    private static final int GAMES_DIFFERENCE_TO_WIN_SET = 2;
    private static final int TIEBREAK_POINTS_TO_WIN = 7;
    private static final int TIEBREAK_POINTS_DIFFERENCE_TO_WIN = 2;
    private static final int SETS_TO_WIN_MATCH = 2;
    private static final int MAX_POSSIBLE_POINTS_PER_GAME = 40;

    public void winPoint(OngoingMatch ongoingMatch, Player player) {
        if (ongoingMatch.getWinner() != null) {
            throw new IllegalStateException("Match is already finished");
        }

        Player opponent = ongoingMatch.getOpponent(player);

        if (ongoingMatch.isTieBreak()) {
            handleTieBreakPoint(ongoingMatch, player, opponent);
        } else {
            handleRegularPoint(ongoingMatch, player, opponent);
        }
    }

    private void handleTieBreakPoint(OngoingMatch ongoingMatch, Player player, Player opponent) {
        ongoingMatch.awardTieBreakPoint(player);
        int playerTieBreakPoints = ongoingMatch.getTieBreakPoints(player);
        int opponentTieBreakPoints = ongoingMatch.getTieBreakPoints(opponent);

        if (isTieBreakOver(playerTieBreakPoints, opponentTieBreakPoints)) {
            playerWonTieBreak(ongoingMatch, player);
        }
    }

    private boolean isTieBreakOver(int playerTieBreakPoints, int opponentTieBreakPoints) {
        return playerTieBreakPoints >= TIEBREAK_POINTS_TO_WIN
               && playerTieBreakPoints - opponentTieBreakPoints >= TIEBREAK_POINTS_DIFFERENCE_TO_WIN;
    }

    private void playerWonTieBreak(OngoingMatch ongoingMatch, Player player) {
        ongoingMatch.winTieBreak(player);
        playerWonSet(ongoingMatch, player);
    }

    private void playerWonSet(OngoingMatch ongoingMatch, Player player) {
        ongoingMatch.winSet(player);

        if (ongoingMatch.getSets(player) == SETS_TO_WIN_MATCH) {
            ongoingMatch.setWinner(player);
        }
    }

    private void handleRegularPoint(OngoingMatch ongoingMatch, Player player, Player opponent) {
        int playerPoints = ongoingMatch.getPoints(player);
        int opponentPoints = ongoingMatch.getPoints(opponent);
        Player advantage = ongoingMatch.getAdvantageStatus();

        if (isDeuce(playerPoints, opponentPoints, advantage)) {
            ongoingMatch.setAdvantage(player);
            return;
        }

        Player currentAdvantage = ongoingMatch.getAdvantageStatus();

        if (currentAdvantage != null) {
            if (currentAdvantage.equals(player)) {
                playerWonGame(ongoingMatch, player, opponent);
            } else {
                ongoingMatch.resetAdvantage();
            }
            return;
        }

        ongoingMatch.awardTennisPoint(player);

        if (ongoingMatch.getPoints(player) > MAX_POSSIBLE_POINTS_PER_GAME) {
            playerWonGame(ongoingMatch, player, opponent);
        }
    }

    private boolean isDeuce(int playerPoints, int opponentPoints, Player advantage) {
        return advantage == null && playerPoints == MAX_POSSIBLE_POINTS_PER_GAME
               && opponentPoints == MAX_POSSIBLE_POINTS_PER_GAME;
    }

    private void playerWonGame(OngoingMatch ongoingMatch, Player player, Player opponent) {
        ongoingMatch.winGame(player);
        checkIfSetFinished(ongoingMatch, player, opponent);
    }

    private void checkIfSetFinished(OngoingMatch ongoingMatch, Player player, Player opponent) {
        int playerGames = ongoingMatch.getGames(player);
        int opponentGames = ongoingMatch.getGames(opponent);

        if (isTieBreak(playerGames, opponentGames)) {
            ongoingMatch.setTieBreak(true);
            return;
        }

        if (isSetWon(playerGames, opponentGames)) {
            playerWonSet(ongoingMatch, player);
        }
    }

    private boolean isSetWon(int playerGames, int opponentGames) {
        return playerGames >= GAMES_TO_WIN_SET && playerGames - opponentGames >= GAMES_DIFFERENCE_TO_WIN_SET;
    }

    private boolean isTieBreak(int playerGames, int opponentGames) {
        return playerGames == GAMES_TO_WIN_SET && opponentGames == GAMES_TO_WIN_SET;
    }
}
