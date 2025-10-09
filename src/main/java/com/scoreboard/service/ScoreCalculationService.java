package com.scoreboard.service;

import com.scoreboard.model.OngoingMatch;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.model.Player;

import java.util.Optional;

public class ScoreCalculationService {
    public static final int MAX_POINTS_PER_GAME = 40;
    public static final int SETS_TO_WIN_MATCH = 2;
    public static final int MIN_TIEBREAK_POINTS_TO_WIN = 7;
    public static final int MIN_ADVANTAGE_TO_WIN = 2;
    public static final int GAMES_FOR_TIEBREAK = 6;
    public static final int GAMES_TO_WIN_SET = 6;

    public void awardPointToPlayer(OngoingMatch ongoingMatch, String playerId) {
        Player pointWinner = findPlayerById(ongoingMatch, playerId);
        updateScore(ongoingMatch, pointWinner);

        if (isMatchFinished(ongoingMatch)) {
            ongoingMatch.setWinner(pointWinner);
        }
    }

    public boolean isMatchFinished(OngoingMatch ongoingMatch) {
        Player firstPlayer = ongoingMatch.getFirstPlayer();
        Player secondPlayer = ongoingMatch.getSecondPlayer();
        return ongoingMatch.getSets(firstPlayer) >= SETS_TO_WIN_MATCH ||
               ongoingMatch.getSets(secondPlayer) >= SETS_TO_WIN_MATCH;
    }

    private Player findPlayerById(OngoingMatch ongoingMatch, String playerId) {
        if (playerId == null || playerId.isBlank()) {
            throw new ValidationException("Player ID is required");
        }

        Player firstPlayer = ongoingMatch.getFirstPlayer();
        Player secondPlayer = ongoingMatch.getSecondPlayer();

        if (playerId.equals(firstPlayer.getId().toString())) {
            return firstPlayer;
        }
        if (playerId.equals(secondPlayer.getId().toString())) {
            return secondPlayer;
        }
        throw new ValidationException("Player with ID " + playerId + " not found in this match");
    }

    private void updateScore(OngoingMatch ongoingMatch, Player pointWinner) {
        if (isMatchFinished(ongoingMatch)) {
            return;
        }

        if (ongoingMatch.isTieBreak()) {
            handleTieBreakPoint(ongoingMatch, pointWinner);
        } else {
            handleRegularPoint(ongoingMatch, pointWinner);
        }

        updateMatchState(ongoingMatch);
    }

    private void handleTieBreakPoint(OngoingMatch ongoingMatch, Player pointWinner) {
        ongoingMatch.awardTieBreakPoint(pointWinner);

        if (isTieBreakWon(ongoingMatch, pointWinner)) {
            finishTieBreak(ongoingMatch, pointWinner);
        }
    }

    private void handleRegularPoint(OngoingMatch ongoingMatch, Player winner) {
        ongoingMatch.awardTennisPoint(winner);

        if (isGameWon(ongoingMatch, winner)) {
            finishGame(ongoingMatch, winner);
        } else {
            checkAndResetToDeuce(ongoingMatch);
        }
    }

    private void checkAndResetToDeuce(OngoingMatch ongoingMatch) {
        Player first = ongoingMatch.getFirstPlayer();
        Player second = ongoingMatch.getSecondPlayer();

        int firstPoints = ongoingMatch.getPoints(first);
        int secondPoints = ongoingMatch.getPoints(second);

        if (firstPoints == secondPoints && firstPoints >= MAX_POINTS_PER_GAME) {
            ongoingMatch.resetToDeuce();
        }
    }

    private boolean isTieBreakWon(OngoingMatch ongoingMatch, Player pointWinner) {
        int winnerTieBreakPoints = ongoingMatch.getTieBreakPoints(pointWinner);
        Player opponent = getOtherPlayer(ongoingMatch, pointWinner);
        int opponentTieBreakPoints = ongoingMatch.getTieBreakPoints(opponent);

        return winnerTieBreakPoints >= MIN_TIEBREAK_POINTS_TO_WIN &&
               (winnerTieBreakPoints - opponentTieBreakPoints) >= MIN_ADVANTAGE_TO_WIN;
    }

    private void finishTieBreak(OngoingMatch ongoingMatch, Player pointWinner) {
        ongoingMatch.awardGame(pointWinner);
        ongoingMatch.awardSet(pointWinner);
        ongoingMatch.resetAllGames();
        ongoingMatch.resetAllTieBreakPoints();
    }

    private void finishGame(OngoingMatch ongoingMatch,
                            Player pointWinner) {
        ongoingMatch.awardGame(pointWinner);
        ongoingMatch.resetAllPoints();

        if (isSetWon(ongoingMatch, pointWinner)) {
            ongoingMatch.awardSet(pointWinner);
            ongoingMatch.resetAllGames();
        }
    }

    private boolean isGameWon(OngoingMatch ongoingMatch, Player winner) {
        int playerPoints = ongoingMatch.getPoints(winner);
        Player opponent = getOtherPlayer(ongoingMatch, winner);
        int opponentPoints = ongoingMatch.getPoints(opponent);

        if (playerPoints > MAX_POINTS_PER_GAME && opponentPoints < MAX_POINTS_PER_GAME) {
            return true;
        }

        if (playerPoints >= MAX_POINTS_PER_GAME && opponentPoints >= MAX_POINTS_PER_GAME) {
            return (playerPoints - opponentPoints) >= MIN_ADVANTAGE_TO_WIN;
        }

        return false;
    }

    private Player getOtherPlayer(OngoingMatch ongoingMatch, Player player) {
        Player first = ongoingMatch.getFirstPlayer();
        Player second = ongoingMatch.getSecondPlayer();
        return player.equals(first) ? second : first;
    }

    private boolean isSetWon(OngoingMatch ongoingMatch, Player pointWinner) {
        Player otherPlayer = getOtherPlayer(ongoingMatch, pointWinner);

        return ongoingMatch.getGames(pointWinner) >= GAMES_TO_WIN_SET &&
               (ongoingMatch.getGames(pointWinner) - ongoingMatch.getGames(otherPlayer)) >= MIN_ADVANTAGE_TO_WIN;
    }

    private void updateMatchState(OngoingMatch ongoingMatch) {
        Player first = ongoingMatch.getFirstPlayer();
        Player second = ongoingMatch.getSecondPlayer();

        boolean shouldBeTieBreak = ongoingMatch.getGames(first) == GAMES_FOR_TIEBREAK &&
                                   ongoingMatch.getGames(second) == GAMES_FOR_TIEBREAK;

        ongoingMatch.setTieBreak(shouldBeTieBreak);

        if (shouldBeTieBreak) {
            ongoingMatch.setAdvantage(null);
        } else {
            Player advantagePlayer = getAdvantagePlayer(ongoingMatch).orElse(null);
            ongoingMatch.setAdvantage(advantagePlayer);
        }
    }

    private Optional<Player> getAdvantagePlayer(OngoingMatch ongoingMatch) {
        Player first = ongoingMatch.getFirstPlayer();
        Player second = ongoingMatch.getSecondPlayer();

        int firstPoints = ongoingMatch.getPoints(first);
        int secondPoints = ongoingMatch.getPoints(second);

        if (firstPoints >= MAX_POINTS_PER_GAME && secondPoints >= MAX_POINTS_PER_GAME) {
            if (firstPoints - secondPoints == 1) {
                return Optional.of(first);
            } else if (secondPoints - firstPoints == 1) {
                return Optional.of(second);
            }
        }

        return Optional.empty();
    }
}
