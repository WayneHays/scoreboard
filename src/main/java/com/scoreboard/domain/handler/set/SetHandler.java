package com.scoreboard.domain.handler.set;

import com.scoreboard.domain.handler.Handler;
import com.scoreboard.domain.handler.PointResult;
import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.TennisPlayer;
import com.scoreboard.domain.rules.SetRules;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SetHandler extends Handler {
    protected final SetRules rules;

    @Override
    protected PointResult doHandle(OngoingMatch match, TennisPlayer scorer) {
        int scorerGames = match.getGames(scorer);
        int opponentGames = match.getGames(match.getOpponent(scorer));

        int newScorerGames = scorerGames + 1;

        if (newScorerGames >= rules.gamesToWin() && newScorerGames - opponentGames >= 2) {
            return PointResult.SET_FINISHED;
        }

        if (scorerGames == rules.gamesToWin() && opponentGames == rules.gamesToWin()) {
            return PointResult.TIE_BREAK_STARTED;
        }

        return PointResult.GAME_FINISHED;
    }

    @Override
    protected boolean shouldPassToNext(PointResult result) {
        return result == PointResult.SET_FINISHED;
    }
}

