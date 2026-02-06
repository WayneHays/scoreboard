package com.scoreboard.domain.handler.game;

import com.scoreboard.domain.handler.Handler;
import com.scoreboard.domain.model.state.PointResult;
import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.state.Points;
import com.scoreboard.domain.model.TennisPlayer;

public class AdvantageGameHandler extends Handler {

    @Override
    protected boolean shouldPassToNext(PointResult result) {
        return result == PointResult.GAME_FINISHED;
    }

    @Override
    protected PointResult doHandle(OngoingMatch match, TennisPlayer scorer) {
        Points scorerPoints = match.getPoints(scorer);
        Points opponentPoints = match.getPoints(match.getOpponent(scorer));

        if (scorerPoints.isAdvantageState()) {
            return PointResult.GAME_FINISHED;
        }

        if (opponentPoints.isAdvantageState()) {
            return PointResult.DEUCE;
        }

        if (scorerPoints.isForty() && opponentPoints.isForty()) {
            return PointResult.ADVANTAGE;
        }

        if (scorerPoints.isForty() && opponentPoints.isLessThanForty()) {
            return PointResult.GAME_FINISHED;
        }

        return PointResult.POINT_AWARDED;
    }
}
