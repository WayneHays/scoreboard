package com.scoreboard.mapper;

import com.scoreboard.dto.MatchResult;
import com.scoreboard.model.OngoingMatch;
import com.scoreboard.model.entity.Player;

public class MatchResultMapper {

    public MatchResult map(OngoingMatch ongoingMatch) {
        Player winner = ongoingMatch.getWinner();
        Player first = ongoingMatch.getFirstPlayer();
        Player second = ongoingMatch.getSecondPlayer();

        return MatchResult.builder()
                .winnerName(winner.getName())
                .firstPlayerName(first.getName())
                .secondPlayerName(second.getName())
                .firstPlayerFinalSets(ongoingMatch.getSets(first))
                .secondPlayerFinalSets(ongoingMatch.getSets(second))
                .firstPlayerRowClass(winner.equals(first) ? "winner" : "")
                .secondPlayerRowClass(winner.equals(second) ? "winner" : "")
                .build();
    }
}
