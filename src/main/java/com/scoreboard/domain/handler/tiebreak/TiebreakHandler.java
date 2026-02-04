package com.scoreboard.domain.handler.tiebreak;

import com.scoreboard.domain.handler.Handler;
import com.scoreboard.domain.handler.PointResult;
import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.TennisPlayer;
import com.scoreboard.domain.rules.TiebreakRules;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TiebreakHandler extends Handler {
    private final TiebreakRules rules;

    @Override
    protected PointResult doHandle(OngoingMatch match, TennisPlayer scorer) {
        int scorerPoints = match.getTieBreakPoints(scorer);
        int opponentPoints = match.getTieBreakPoints(match.getOpponent(scorer));

        int newScorerPoints = scorerPoints + 1;

        if (newScorerPoints >= rules.pointsToWin() && newScorerPoints - opponentPoints >= 2) {
            return PointResult.SET_FINISHED;
        }

        return PointResult.TIE_BREAK_POINT_AWARDED;
    }

    @Override
    protected boolean shouldPassToNext(PointResult result) {
        return result == PointResult.SET_FINISHED;
    }
}
