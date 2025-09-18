package com.scoreboard.mapper;

import com.scoreboard.dto.MatchResult;
import com.scoreboard.dto.OngoingMatch;
import com.scoreboard.model.Player;

public class MatchResultMapper {

    public MatchResult map(OngoingMatch ongoingMatch) {
        Player winner = ongoingMatch.getMatch().getWinner();
        boolean firstPlayerWins = winner.equals(ongoingMatch.getMatch().getFirstPlayer());

        return MatchResult.builder()
                .winnerName(winner.getName())
                .firstPlayerName(ongoingMatch.getMatch().getFirstPlayer().getName())
                .secondPlayerName(ongoingMatch.getMatch().getSecondPlayer().getName())
                .firstPlayerFinalSets(ongoingMatch.getScore().getSets(ongoingMatch.getMatch().getFirstPlayer()))
                .secondPlayerFinalSets(ongoingMatch.getScore().getSets(ongoingMatch.getMatch().getSecondPlayer()))
                .firstPlayerRowClass(firstPlayerWins ? "winner" : "")
                .secondPlayerRowClass(!firstPlayerWins ? "winner" : "")
                .build();
    }
}
