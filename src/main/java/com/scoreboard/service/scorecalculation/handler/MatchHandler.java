package com.scoreboard.service.scorecalculation.handler;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.ongoingmatch.OngoingMatch;
import com.scoreboard.service.scorecalculation.rules.MatchRules;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MatchHandler extends AbstractHandler {
    private final MatchRules matchRules;

    @Override
    protected void doHandle(OngoingMatch ongoingMatch, Player scorer) {
        int scorerSets = ongoingMatch.getSets(scorer);

        if (scorerSets >= matchRules.setsToWinMatch()) {
            ongoingMatch.setWinner(scorer);
        }
    }
}
