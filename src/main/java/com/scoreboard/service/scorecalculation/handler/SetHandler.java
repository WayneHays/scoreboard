package com.scoreboard.service.scorecalculation.handler;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.service.scorecalculation.rules.SetRules;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SetHandler extends AbstractHandler {
    private final SetRules setRules;

    @Override
    protected void doHandle(OngoingMatch ongoingMatch, Player scorer) {
        Player opponent = ongoingMatch.getOpponent(scorer);
        int scorerGames = ongoingMatch.getGames(scorer);
        int opponentGames = ongoingMatch.getGames(opponent);

        if (shouldActivateTieBreak(scorerGames, opponentGames)) {
            ongoingMatch.setTieBreak(true);
            return;
        }

        if (isSetWon(scorerGames, opponentGames)) {
            ongoingMatch.awardSetTo(scorer);
            callNext(ongoingMatch, scorer);
        }
    }

    private boolean shouldActivateTieBreak(int scorerGames, int opponentGames) {
        return setRules.isTiebreakEnabled() &&
               scorerGames == setRules.gamesToWinSet() &&
               opponentGames == setRules.gamesToWinSet();
    }

    private boolean isSetWon(int scorerGames, int opponentGames) {
        int gamesToWin = setRules.gamesToWinSet();
        int minDifference = setRules.minGamesDifferenceToWinSet();

        if (setRules.isTiebreakEnabled() &&
            scorerGames == gamesToWin + 1 &&
            opponentGames == gamesToWin) {
            return true;
        }

        if (scorerGames >= gamesToWin) {
            int difference = scorerGames - opponentGames;
            return difference >= minDifference;
        }

        return false;
    }
}
