package com.scoreboard.service;

import com.scoreboard.dto.GameState;
import com.scoreboard.model.*;

import java.util.Optional;

public class ScoreCalculationService {
    public static final int MAX_POINTS_PER_GAME = 40;
    public static final int SETS_TO_WIN_MATCH = 2;
    public static final int MIN_TIEBREAK_POINTS_TO_WIN = 7;
    public static final int MIN_ADVANTAGE_TO_WIN = 2;
    public static final int GAMES_FOR_TIEBREAK = 6;
    public static final int GAMES_TO_WIN_SET = 6;

    private static final ScoreCalculationService INSTANCE = new ScoreCalculationService();

    public static ScoreCalculationService getInstance() {
        return INSTANCE;
    }

    public GameState calculate(MatchWithScore matchWithScore, Player pointWinner) {
        Score score = matchWithScore.score();
        Match match = matchWithScore.match();
        Player firstPlayer = match.getFirstPlayer();
        Player secondPlayer = match.getSecondPlayer();

        if (isMatchFinished(score, firstPlayer, secondPlayer)) {
            return createGameState(score, firstPlayer, secondPlayer);
        }

        if (isTieBreak(score, firstPlayer, secondPlayer)) {
            calculateTieBreak(pointWinner, score, firstPlayer, secondPlayer);
        } else {
            calculateNonTieBreak(pointWinner, score, firstPlayer, secondPlayer);
        }

        return createGameState(score, firstPlayer, secondPlayer);
    }

    public boolean isMatchFinished(Score score, Player first, Player second) {
        return score.getSets(first) >= SETS_TO_WIN_MATCH ||
               score.getSets(second) >= SETS_TO_WIN_MATCH;
    }

    public boolean isTieBreak(Score score, Player firstPlayer, Player secondPlayer) {
        return score.getGames(firstPlayer) == GAMES_FOR_TIEBREAK &&
               score.getGames(secondPlayer) == GAMES_FOR_TIEBREAK;
    }

    public boolean isDeuce(Score score, Player firstPlayer, Player secondPlayer) {
        return score.getPoints(firstPlayer) == MAX_POINTS_PER_GAME &&
               score.getPoints(secondPlayer) == MAX_POINTS_PER_GAME;
    }

    public Optional<Player> getAdvantagePlayer(Score score, Player firstPlayer, Player secondPlayer) {
        int firstPoints = score.getPoints(firstPlayer);
        int secondPoints = score.getPoints(secondPlayer);

        if (firstPoints < MAX_POINTS_PER_GAME || secondPoints < MAX_POINTS_PER_GAME) {
            return Optional.empty();
        }

        if (firstPoints == secondPoints) {
            return Optional.empty();
        }

        if (firstPoints > secondPoints) {
            return Optional.of(firstPlayer);
        } else {
            return Optional.of(secondPlayer);
        }
    }

    private void calculateTieBreak(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        int currentTieBreakPoints = score.getTieBreakPoints(pointWinner);
        score.setTieBreakPoints(pointWinner, currentTieBreakPoints + 1);

        if (isTieBreakWon(pointWinner, score, firstPlayer, secondPlayer)) {
            handleTieBreakWin(pointWinner, score, firstPlayer, secondPlayer);
        }
    }

    private void handleTieBreakWin(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        winGame(pointWinner, score);
        winSet(pointWinner, score);
        score.resetGames(firstPlayer, secondPlayer);
        score.resetTieBreakPoints(firstPlayer, secondPlayer);
    }

    private void calculateNonTieBreak(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        boolean wasDeuceBefore = score.getPoints(firstPlayer) >= MAX_POINTS_PER_GAME &&
                                 score.getPoints(secondPlayer) >= MAX_POINTS_PER_GAME;

        Optional<Player> advantagePlayerBefore = Optional.empty();
        if (wasDeuceBefore) {
            advantagePlayerBefore = getAdvantagePlayer(score, firstPlayer, secondPlayer);
        }

        winPoint(pointWinner, score);

        if (wasDeuceBefore) {
            calculateDeuceLogic(pointWinner, score, firstPlayer, secondPlayer, advantagePlayerBefore);
        } else {
            calculateRegularGame(pointWinner, score, firstPlayer, secondPlayer);
        }

        System.out.println("Points after logic: " + score.getPoints(firstPlayer) + ":" + score.getPoints(secondPlayer));
        System.out.println("Games after logic: " + score.getGames(firstPlayer) + ":" + score.getGames(secondPlayer));
    }

    private void calculateDeuceLogic(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer, Optional<Player> advantagePlayerBefore) {
        System.out.println("=== calculateDeuceLogic ===");
        System.out.println("Points before: " + score.getPoints(firstPlayer) + ":" + score.getPoints(secondPlayer));
        System.out.println("advantagePlayerBefore: " + advantagePlayerBefore.orElse(null));

        if (advantagePlayerBefore.isEmpty()) {
            return;
        }

        if (advantagePlayerBefore.get().equals(pointWinner)) {
            winGame(pointWinner, score);
            score.resetPoints(firstPlayer, secondPlayer);

            if (shouldCheckSetAndMatch(score, firstPlayer, secondPlayer)) {
                checkIfSetAndMatchWon(pointWinner, score, firstPlayer, secondPlayer);
            }
            return;
        }
        score.resetPoints(firstPlayer, secondPlayer);
        score.setPoints(firstPlayer, MAX_POINTS_PER_GAME);
        score.setPoints(secondPlayer, MAX_POINTS_PER_GAME);
    }

    private void calculateRegularGame(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        boolean gameWon = isGameWon(pointWinner, score);

        if (!gameWon) {
            return;
        }
        winGame(pointWinner, score);
        score.resetPoints(firstPlayer, secondPlayer);

        if (shouldCheckSetAndMatch(score, firstPlayer, secondPlayer)) {
            checkIfSetAndMatchWon(pointWinner, score, firstPlayer, secondPlayer);
        }
    }

    private boolean isGameWon(Player pointWinner, Score score) {
        return score.getPoints(pointWinner) > MAX_POINTS_PER_GAME;
    }

    private boolean isTieBreakWon(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        return score.getTieBreakPoints(pointWinner) >= MIN_TIEBREAK_POINTS_TO_WIN &&
               Math.abs(score.getTieBreakPoints(firstPlayer) - score.getTieBreakPoints(secondPlayer)) >= MIN_ADVANTAGE_TO_WIN;
    }

    private boolean shouldCheckSetAndMatch(Score score, Player firstPlayer, Player secondPlayer) {
        return score.getGames(firstPlayer) != GAMES_FOR_TIEBREAK ||
               score.getGames(secondPlayer) != GAMES_FOR_TIEBREAK;
    }

    private void checkIfSetAndMatchWon(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        if (isSetWon(pointWinner, score, firstPlayer, secondPlayer)) {
            winSet(pointWinner, score);
            score.resetGames(firstPlayer, secondPlayer);
        }
    }

    private boolean isSetWon(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        return score.getGames(pointWinner) >= GAMES_TO_WIN_SET &&
               Math.abs(score.getGames(firstPlayer) - score.getGames(secondPlayer)) >= MIN_ADVANTAGE_TO_WIN;
    }

    private void winPoint(Player player, Score score) {
        int currentPlayerPoints = score.getPoints(player);

        switch (currentPlayerPoints) {
            case 0 -> score.setPoints(player, 15);
            case 15 -> score.setPoints(player, 30);
            case 30 -> score.setPoints(player, 40);
            default -> score.setPoints(player, currentPlayerPoints + 1);
        }
    }

    private void winGame(Player player, Score score) {
        int currentPlayerGames = score.getGames(player);
        score.setGames(player, currentPlayerGames + 1);
    }

    private void winSet(Player player, Score score) {
        int currentPlayerSets = score.getSets(player);
        score.setSets(player, currentPlayerSets + 1);
    }

    private GameState createGameState(Score score, Player firstPlayer, Player secondPlayer) {
        boolean isTieBreak = isTieBreak(score, firstPlayer, secondPlayer);
        Player advantagePlayer = getAdvantagePlayer(score, firstPlayer, secondPlayer)
                .orElse(null);

        return new GameState(score, isTieBreak, advantagePlayer);
    }

    public GameState getCurrentGameState(MatchWithScore matchWithScore) {
        Score score = matchWithScore.score();
        Player firstPlayer = matchWithScore.match().getFirstPlayer();
        Player secondPlayer = matchWithScore.match().getSecondPlayer();

        return createGameState(score, firstPlayer, secondPlayer);
    }
}
