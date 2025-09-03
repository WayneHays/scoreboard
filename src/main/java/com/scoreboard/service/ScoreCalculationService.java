package com.scoreboard.service;

import com.scoreboard.model.*;

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

    public Score calculate(MatchWithScore matchWithScore, Player pointWinner) {
        Score score = matchWithScore.score();
        Match match = matchWithScore.match();
        Player firstPlayer = match.getFirstPlayer();
        Player secondPlayer = match.getSecondPlayer();

        if (score.isMatchFinished()) {
            return score;
        }

        if (score.isTieBreak()) {
            calculateTieBreak(pointWinner, score, firstPlayer, secondPlayer);

            if (!score.isTieBreak()) {
                return score;
            }
        } else {
            calculateNonTieBreak(pointWinner, score, firstPlayer, secondPlayer);
        }
        return score;
    }

    private boolean isMatchFinished(Player pointWinner, Score score) {
        return score.getSets(pointWinner) >= SETS_TO_WIN_MATCH;
    }

    private void calculateTieBreak(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        int currentTieBreakPoints = score.getTieBreakPoints(pointWinner);
        score.setTieBreakPoints(pointWinner, currentTieBreakPoints + 1);

        if (isTieBreakWon(pointWinner, score, firstPlayer, secondPlayer)) {
            score.setTieBreak(false);
            handleTieBreakWin(pointWinner, score, firstPlayer, secondPlayer);
        }
    }

    private void handleTieBreakWin(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        winGame(pointWinner, score);
        winSet(pointWinner, score);
        score.resetGames(firstPlayer, secondPlayer);
        score.resetTieBreakPoints(firstPlayer, secondPlayer);

        if (isMatchFinished(pointWinner, score)) {
            score.setMatchFinished(true);
        }
    }

    private void calculateNonTieBreak(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        winPoint(pointWinner, score);

        if (isDeuce(score, firstPlayer, secondPlayer) && !score.isDeuce()) {
            score.setDeuce(true);
            return;
        }

        if (score.isDeuce()) {
            calculateDeuce(pointWinner, score, firstPlayer, secondPlayer);
        } else {
            calculateRegularGame(pointWinner, score, firstPlayer, secondPlayer);
        }
    }

    private void calculateDeuce(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        if (score.getAdvantage() == null) {
            score.setAdvantage(pointWinner);
        } else if (score.getAdvantage().equals(pointWinner)) {
            winGame(pointWinner, score);
            score.resetPoints(firstPlayer, secondPlayer);
            score.setDeuce(false);
            score.setAdvantage(null);

            if (isTieBreakRequired(score, firstPlayer, secondPlayer)) {
                score.setTieBreak(true);
            }
            checkIfSetAndMatchWon(pointWinner, score, firstPlayer, secondPlayer);
        } else {
            score.setAdvantage(null);
        }
    }

    private void calculateRegularGame(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        if (isGameWon(pointWinner, score)) {
            winGame(pointWinner, score);
            score.resetPoints(firstPlayer, secondPlayer);

            if (isTieBreakRequired(score, firstPlayer, secondPlayer)) {
                score.setTieBreak(true);
            } else {
                checkIfSetAndMatchWon(pointWinner, score, firstPlayer, secondPlayer);
            }
        }
    }

    private boolean isGameWon(Player pointWinner, Score score) {
        return score.getPoints(pointWinner) > MAX_POINTS_PER_GAME;
    }

    private boolean isTieBreakWon(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        return score.getTieBreakPoints(pointWinner) >= MIN_TIEBREAK_POINTS_TO_WIN &&
               Math.abs(score.getTieBreakPoints(firstPlayer) - score.getTieBreakPoints(secondPlayer)) >= MIN_ADVANTAGE_TO_WIN;
    }

    private boolean isTieBreakRequired(Score score, Player firstPlayer, Player secondPlayer) {
        return score.getGames(firstPlayer) == GAMES_FOR_TIEBREAK && score.getGames(secondPlayer) == GAMES_FOR_TIEBREAK;
    }

    private void checkIfSetAndMatchWon(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        if (isSetWon(pointWinner, score, firstPlayer, secondPlayer)) {
            winSet(pointWinner, score);
            score.resetGames(firstPlayer, secondPlayer);

            if (isMatchFinished(pointWinner, score)) {
                score.setMatchFinished(true);
            }
        }
    }

    private boolean isSetWon(Player pointWinner, Score score, Player firstPlayer, Player secondPlayer) {
        return score.getGames(pointWinner) >= GAMES_TO_WIN_SET &&
               Math.abs(score.getGames(firstPlayer) - score.getGames(secondPlayer)) >= MIN_ADVANTAGE_TO_WIN;
    }

    private void winPoint(Player player, Score score) {
        int currentPlayerPoints = score.getPoints(player);

        if (score.isDeuce() && currentPlayerPoints >= 40) {
            return;
        }

        switch (currentPlayerPoints) {
            case 0 -> {
                score.setPoints(player, 15);
            }
            case 15 -> {
                score.setPoints(player, 30);
            }
            case 30 -> {
                score.setPoints(player, 40);
            }
            default -> {
                score.setPoints(player, currentPlayerPoints + 1);
            }
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

    private boolean isDeuce(Score score, Player firstPlayer, Player secondPlayer) {
        return score.getPoints(firstPlayer) == MAX_POINTS_PER_GAME && score.getPoints(secondPlayer) == MAX_POINTS_PER_GAME;
    }
}
