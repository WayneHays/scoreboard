package com.scoreboard.service;

import com.scoreboard.dto.OngoingMatch;
import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;

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

    public void calculate(OngoingMatch ongoingMatch, Player pointWinner) {
        Score score = ongoingMatch.getScore();
        Player firstPlayer = ongoingMatch.getMatch().getFirstPlayer();
        Player secondPlayer = ongoingMatch.getMatch().getSecondPlayer();

        if (isMatchFinished(score, firstPlayer, secondPlayer)) {
            return;
        }

        if (ongoingMatch.isTieBreak()) {
            processTieBreakPoint(score, pointWinner, firstPlayer, secondPlayer);
        } else {
            processRegularPoint(score, pointWinner, firstPlayer, secondPlayer);
        }

        updateMatchState(ongoingMatch, firstPlayer, secondPlayer);
    }

    private void updateMatchState(OngoingMatch ongoingMatch, Player firstPlayer, Player secondPlayer) {
        Score score = ongoingMatch.getScore();

        boolean shouldBeTieBreak = isTieBreak(score, firstPlayer, secondPlayer);
        Optional<Player> advantagePlayer = getAdvantagePlayer(score, firstPlayer, secondPlayer);

        ongoingMatch.setTieBreak(shouldBeTieBreak);
        ongoingMatch.setAdvantage(shouldBeTieBreak ? null : advantagePlayer.orElse(null));
    }

    private void processTieBreakPoint(Score score, Player winner, Player firstPlayer, Player secondPlayer) {
        score.awardTieBreakPoint(winner);

        if (score.getTieBreakPoints(winner) >= MIN_TIEBREAK_POINTS_TO_WIN &&
            Math.abs(score.getTieBreakPoints(firstPlayer) - score.getTieBreakPoints(secondPlayer)) >= MIN_ADVANTAGE_TO_WIN) {

            score.awardGame(winner);
            score.awardSet(winner);
            score.resetAllGames();
            score.resetAllTieBreakPoints();
        }
    }

    private void processRegularPoint(Score score, Player winner, Player firstPlayer, Player secondPlayer) {
        Optional<Player> advantagePlayerBefore = getAdvantagePlayer(score, firstPlayer, secondPlayer);
        boolean wasDeuce = isDeuce(score, firstPlayer, secondPlayer);

        score.awardTennisPoint(winner);

        if (wasDeuce || advantagePlayerBefore.isPresent()) {
            handleDeuceScenario(score, winner, advantagePlayerBefore, firstPlayer, secondPlayer);
        } else if (score.getPoints(winner) > MAX_POINTS_PER_GAME) {
            completeGame(score, winner, firstPlayer, secondPlayer);
        }
    }

    private void handleDeuceScenario(Score score, Player winner, Optional<Player> advantagePlayerBefore,
                                     Player firstPlayer, Player secondPlayer) {
        if (advantagePlayerBefore.isPresent()) {
            if (advantagePlayerBefore.get().equals(winner)) {
                completeRegularGame(score, winner, firstPlayer, secondPlayer);
            } else {
                score.resetToDeuce();
            }
        }
    }

    private void completeGame(Score score, Player winner, Player firstPlayer, Player secondPlayer) {
        score.awardGame(winner);
        score.resetAllPoints();

        if (isNotTieBreakSituation(score, firstPlayer, secondPlayer) &&
            isSetWon(score, winner, firstPlayer, secondPlayer)) {

            score.awardSet(winner);
            score.resetAllGames();
        }
    }

    private void completeRegularGame(Score score, Player winner, Player firstPlayer, Player secondPlayer) {
        score.awardGame(winner);
        score.resetAllPoints();

        if (isSetWon(score, winner, firstPlayer, secondPlayer)) {
            score.awardSet(winner);
            score.resetAllGames();
        }
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
        return score.getPoints(firstPlayer) >= MAX_POINTS_PER_GAME &&
               score.getPoints(secondPlayer) >= MAX_POINTS_PER_GAME &&
               score.getPoints(firstPlayer) == score.getPoints(secondPlayer);
    }

    public Optional<Player> getAdvantagePlayer(Score score, Player firstPlayer, Player secondPlayer) {
        int firstPoints = score.getPoints(firstPlayer);
        int secondPoints = score.getPoints(secondPlayer);

        if (firstPoints < MAX_POINTS_PER_GAME || secondPoints < MAX_POINTS_PER_GAME ||
            firstPoints == secondPoints) {
            return Optional.empty();
        }

        return firstPoints > secondPoints ? Optional.of(firstPlayer) : Optional.of(secondPlayer);
    }

    public Player getMatchWinner(Match match, Score score) {
        int firstPlayerSets = score.getSets(match.getFirstPlayer());
        int secondPlayerSets = score.getSets(match.getSecondPlayer());

        return firstPlayerSets > secondPlayerSets ? match.getFirstPlayer() : match.getSecondPlayer();
    }

    private boolean isSetWon(Score score, Player winner, Player firstPlayer, Player secondPlayer) {
        return score.getGames(winner) >= GAMES_TO_WIN_SET &&
               Math.abs(score.getGames(firstPlayer) - score.getGames(secondPlayer)) >= MIN_ADVANTAGE_TO_WIN;
    }

    private boolean isNotTieBreakSituation(Score score, Player firstPlayer, Player secondPlayer) {
        return !isTieBreak(score, firstPlayer, secondPlayer);
    }
}
