package com.scoreboard.domain.handler.match;

import com.scoreboard.domain.handler.Handler;
import com.scoreboard.domain.model.state.PointResult;
import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.TennisPlayer;
import com.scoreboard.domain.rules.MatchRules;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MatchHandler extends Handler {
    protected final MatchRules rules;

    @Override
    protected PointResult doHandle(OngoingMatch match, TennisPlayer scorer) {
        int scorerSets = match.getSets(scorer);
        int newScorerSets = scorerSets + 1;

        if (newScorerSets >= rules.setsToWin()) {
            return PointResult.MATCH_FINISHED;
        }

        return PointResult.SET_FINISHED;
    }

    @Override
    protected boolean shouldPassToNext(PointResult result) {
        return false;
    }
}
