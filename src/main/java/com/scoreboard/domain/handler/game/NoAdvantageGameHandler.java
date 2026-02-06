package com.scoreboard.domain.handler.game;

import com.scoreboard.domain.handler.Handler;
import com.scoreboard.domain.model.state.PointResult;
import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.state.Points;
import com.scoreboard.domain.model.TennisPlayer;

public class NoAdvantageGameHandler extends Handler {

    @Override
    protected PointResult doHandle(OngoingMatch match, TennisPlayer scorer) {
        Points scorerPoints = match.getPoints(scorer);

        if (scorerPoints.isForty()) {
            return PointResult.GAME_FINISHED;
        }

        return PointResult.POINT_AWARDED;
    }

    @Override
    protected boolean shouldPassToNext(PointResult result) {
        return result == PointResult.GAME_FINISHED;
    }
}
