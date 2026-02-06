package com.scoreboard.domain.handler.set;

import com.scoreboard.domain.handler.Handler;
import com.scoreboard.domain.model.state.PointResult;
import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.TennisPlayer;
import com.scoreboard.domain.rules.SetRules;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SetHandler extends Handler {
    private static final int MIN_DIFFERENCE = 2;
    protected final SetRules rules;

    @Override
    protected PointResult doHandle(OngoingMatch match, TennisPlayer scorer) {
        int scorerGames = match.getGames(scorer);
        int opponentGames = match.getGames(match.getOpponent(scorer));

        int newScorerGames = scorerGames + 1;

        if (isSetFinished(newScorerGames, opponentGames)) {
            return PointResult.SET_FINISHED;
        }

        if (newScorerGames == rules.gamesToWin() && opponentGames == rules.gamesToWin()) {
            return PointResult.TIE_BREAK_STARTED;
        }

        return PointResult.GAME_FINISHED;
    }

    private boolean isSetFinished(int newScorerGames, int opponentGames) {
        return (newScorerGames == rules.gamesToWin() && newScorerGames - opponentGames >= MIN_DIFFERENCE)
               || newScorerGames > rules.gamesToWin();
    }

    @Override
    protected boolean shouldPassToNext(PointResult result) {
        return result == PointResult.SET_FINISHED;
    }
}

