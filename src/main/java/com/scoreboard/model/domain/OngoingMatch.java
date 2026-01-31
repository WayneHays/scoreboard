package com.scoreboard.model.domain;

import com.scoreboard.service.scorecalculation.PointResult;
import com.scoreboard.service.scorecalculation.handler.Handler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Getter
public class OngoingMatch {
    private final TennisPlayer firstPlayer;
    private final TennisPlayer  secondPlayer;
    private final Handler handlerChain;
    private final PlayerScore firstPlayerScore;
    private final PlayerScore secondPlayerScore;
    private MatchState matchState;

    public OngoingMatch(TennisPlayer  firstPlayer, TennisPlayer  secondPlayer, Handler handlerChain) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.handlerChain = handlerChain;
        this.firstPlayerScore = new PlayerScore();
        this.secondPlayerScore = new PlayerScore();
        this.matchState = MatchState.REGULAR;
    }

    public void awardPoint(TennisPlayer  scorer) {
        PointResult pointResult = handlerChain.handle(this, scorer);
        PlayerScore pointWinnerScore = scorer.equals(firstPlayer) ? firstPlayerScore : secondPlayerScore;

        switch (pointResult) {
            case POINT_AWARDED -> {
                pointWinnerScore.addPoint();
                matchState = MatchState.REGULAR;
            }

            case GAME_FINISHED -> {
                resetPointsForBoth();
                resetTieBreakPointsForBoth();
                pointWinnerScore.addGame();
                matchState = MatchState.REGULAR;
            }

            case ADVANTAGE -> {
                pointWinnerScore.addPoint();
                matchState = MatchState.ADVANTAGE;
            }

            case DEUCE -> {
                resetToDeuce();
                matchState = MatchState.REGULAR;
            }

            case SET_FINISHED -> {
                resetPointsForBoth();
                resetGamesForBoth();
                resetTieBreakPointsForBoth();
                pointWinnerScore.addSet();
                matchState = MatchState.REGULAR;
            }

            case TIE_BREAK_STARTED -> {
                resetPointsForBoth();
                matchState = MatchState.TIEBREAK;
            }

            case TIE_BREAK_POINT_AWARDED -> {
                pointWinnerScore.addTieBreakPoint();
                matchState = MatchState.TIEBREAK;
            }

            case MATCH_OVER -> {
                resetPointsForBoth();
                resetGamesForBoth();
                resetTieBreakPointsForBoth();
                pointWinnerScore.addSet();
                matchState = MatchState.FINISHED;
            }
        }
    }

    public Optional<String> getWinnerName() {
        if (isFinished()) {
            return Optional.of(getFirstPlayerSets() > getSecondPlayerSets() ? firstPlayer.name() : secondPlayer.name());
        }
        return Optional.empty();
    }

    public Optional<TennisPlayer> findPlayerByName(String name) {

    }

    public boolean isTieBreak() {
        return matchState == MatchState.TIEBREAK;
    }

    public boolean isFinished() {
        return matchState == MatchState.FINISHED;
    }

    public boolean isAdvantage() {
        return matchState == MatchState.ADVANTAGE;
    }

    public String getFirstPlayerName() {
        return firstPlayer.name();
    }

    public String getSecondPlayerName() {
        return secondPlayer.name();
    }

    public String getFirstPlayerPoints() {
        return firstPlayerScore.getPoints().getValue();
    }

    public String getSecondPlayerPoints() {
        return secondPlayerScore.getPoints().getValue();
    }

    public int getFirstPlayerGames() {
        return firstPlayerScore.getGames();
    }

    public int getSecondPlayerGames() {
        return secondPlayerScore.getGames();
    }

    public int getFirstPlayerTieBreakPoints() {
        return firstPlayerScore.getTieBreakPoints();
    }

    public int getSecondPlayerTieBreakPoints() {
        return secondPlayerScore.getTieBreakPoints();
    }

    public int getFirstPlayerSets() {
        return firstPlayerScore.getSets();
    }

    public int getSecondPlayerSets() {
        return secondPlayerScore.getSets();
    }

    private void resetToDeuce() {
        forBothScores(PlayerScore::resetToDeuce);
    }

    private void resetPointsForBoth() {
        forBothScores(PlayerScore::resetPoints);
    }

    private void resetGamesForBoth() {
        forBothScores(PlayerScore::resetGames);
    }

    private void resetTieBreakPointsForBoth() {
        forBothScores(PlayerScore::resetTieBreakPoints);
    }

    private void forBothScores(Consumer<PlayerScore> action) {
        action.accept(firstPlayerScore);
        action.accept(secondPlayerScore);
    }
}
