package com.scoreboard.domain.model;

import com.scoreboard.domain.handler.Handler;
import com.scoreboard.domain.model.state.OngoingMatchState;
import com.scoreboard.domain.model.state.PointResult;
import com.scoreboard.domain.model.state.Points;
import com.scoreboard.exception.PlayerNotInMatchException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Getter
public class OngoingMatch {
    private static final String PLAYER_NOT_IN_MATCH = "Player not in match: ";

    private final TennisPlayer firstPlayer;
    private final TennisPlayer secondPlayer;
    private final Handler tieBreakChain;
    private final Handler regularChain;
    private final PlayerScore firstPlayerScore;
    private final PlayerScore secondPlayerScore;
    private OngoingMatchState ongoingMatchState;

    public OngoingMatch(TennisPlayer firstPlayer, TennisPlayer secondPlayer,
                        Handler regularChain, Handler tieBreakChain) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.tieBreakChain = tieBreakChain;
        this.regularChain = regularChain;
        this.firstPlayerScore = new PlayerScore();
        this.secondPlayerScore = new PlayerScore();
        this.ongoingMatchState = OngoingMatchState.REGULAR;
    }

    public void processPoint(TennisPlayer scorer) {
        Handler chain = isTieBreak() ? tieBreakChain : regularChain;
        PointResult pointResult = chain.handle(this, scorer);
        applyResult(pointResult, scorer);
    }

    private void applyResult(PointResult pointResult, TennisPlayer scorer) {
        PlayerScore score = getScore(scorer);

        switch (pointResult) {
            case POINT_AWARDED, ADVANTAGE -> {
                score.addPoint();

                if (isTieBreak()) {
                    ongoingMatchState = OngoingMatchState.TIEBREAK;
                    return;
                }
                ongoingMatchState = OngoingMatchState.REGULAR;
            }

            case GAME_FINISHED -> {
                resetPointsForBoth();
                score.addGame();
                ongoingMatchState = OngoingMatchState.REGULAR;
            }

            case DEUCE -> {
                resetToDeuce();
                ongoingMatchState = OngoingMatchState.REGULAR;
            }

            case SET_FINISHED -> {
                resetPointsForBoth();
                resetGamesForBoth();
                resetTieBreakPointsForBoth();
                score.addSet();
                ongoingMatchState = OngoingMatchState.REGULAR;
            }

            case TIE_BREAK_STARTED -> {
                resetPointsForBoth();
                score.addGame();
                ongoingMatchState = OngoingMatchState.TIEBREAK;
            }

            case TIE_BREAK_POINT_AWARDED -> {
                score.addTieBreakPoint();
                ongoingMatchState = OngoingMatchState.TIEBREAK;
            }

            case MATCH_FINISHED -> {
                resetPointsForBoth();
                resetGamesForBoth();
                resetTieBreakPointsForBoth();
                score.addSet();
                ongoingMatchState = OngoingMatchState.FINISHED;
            }
        }
    }

    public Optional<TennisPlayer> ensurePlayer(String name) {
        if (firstPlayer.name().equalsIgnoreCase(name)) {
            return Optional.of(firstPlayer);
        }

        if (secondPlayer.name().equalsIgnoreCase(name)) {
            return Optional.of(secondPlayer);
        }

        return Optional.empty();
    }

    public boolean isTieBreak() {
        return ongoingMatchState == OngoingMatchState.TIEBREAK;
    }

    public boolean isFinished() {
        return ongoingMatchState == OngoingMatchState.FINISHED;
    }

    public Optional<String> getWinnerName() {
        if (isFinished()) {
            return Optional.of(getSets(firstPlayer) > getSets(secondPlayer) ? firstPlayer.name() : secondPlayer.name());
        }
        return Optional.empty();
    }

    public TennisPlayer getOpponent(TennisPlayer player) {
        if (player.equals(firstPlayer)) {
            return secondPlayer;
        }
        if (player.equals(secondPlayer)) {
            return firstPlayer;
        }
        throw new PlayerNotInMatchException(PLAYER_NOT_IN_MATCH + player.name());
    }

    public Points getPoints(TennisPlayer player) {
        return getScore(player).getPoints();
    }

    public int getGames(TennisPlayer player) {
        return getScore(player).getGames();
    }

    public int getSets(TennisPlayer player) {
        return getScore(player).getSets();
    }

    public int getTieBreakPoints(TennisPlayer player) {
        return getScore(player).getTieBreakPoints();
    }

    private PlayerScore getScore(TennisPlayer player) {
        if (player.equals(firstPlayer)) {
            return firstPlayerScore;
        }
        if (player.equals(secondPlayer)) {
            return secondPlayerScore;
        }
        throw new PlayerNotInMatchException(PLAYER_NOT_IN_MATCH + player.name());
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
