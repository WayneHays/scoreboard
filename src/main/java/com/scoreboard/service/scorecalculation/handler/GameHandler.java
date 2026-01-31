package com.scoreboard.service.scorecalculation.handler;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.service.scorecalculation.PointResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class GameHandler extends AbstractHandler {

    @Override
    protected final void doHandle(Player scorer) {
        if (ongoingMatch.isTieBreak()) {
            callNext(scorer);
            return;
        }

        Player opponent = ongoingMatch.getOpponent(scorer);
        PointResult pointResult = handleGamePoint(ongoingMatch, scorer, opponent);

        if (pointResult == PointResult.GAME_FINISHED) {
            callNext(scorer);
        }
    }

    protected abstract PointResult handleGamePoint(OngoingMatch ongoingMatch, Player scorer, Player opponent);
}
