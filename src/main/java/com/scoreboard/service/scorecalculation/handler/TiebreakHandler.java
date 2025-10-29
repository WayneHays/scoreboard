package com.scoreboard.service.scorecalculation.handler;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.ongoingmatch.OngoingMatch;
import com.scoreboard.service.scorecalculation.rules.TiebreakRules;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TiebreakHandler extends AbstractHandler{
    private final TiebreakRules tiebreakRules;

    @Override
    protected void doHandle(OngoingMatch ongoingMatch, Player scorer) {
        if (!ongoingMatch.isTieBreak()) {
            callNext(ongoingMatch, scorer);
            return;
        }

        Player opponent = ongoingMatch.getOpponent(scorer);
        ongoingMatch.awardTieBreakPointTo(scorer);

        if (isTieBreakFinished(ongoingMatch, scorer, opponent)) {
            ongoingMatch.awardGameTo(scorer);
            ongoingMatch.setTieBreak(false);
            callNext(ongoingMatch, scorer);
        }
    }

    private boolean isTieBreakFinished(OngoingMatch match, Player scorer, Player opponent) {
        int scorerPoints = match.getTieBreakPoints(scorer);
        int opponentPoints = match.getTieBreakPoints(opponent);

        return scorerPoints >= tiebreakRules.pointsToWinTieBreak()
               && (scorerPoints - opponentPoints) >= tiebreakRules.minDifferenceToWinTieBreak();
    }
}
