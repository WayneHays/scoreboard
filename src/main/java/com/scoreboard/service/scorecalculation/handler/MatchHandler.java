package com.scoreboard.service.scorecalculation.handler;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.tennisrules.MatchRules;
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
