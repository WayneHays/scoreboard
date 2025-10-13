package com.scoreboard.mapper;

import com.scoreboard.dto.MatchResult;
import com.scoreboard.model.OngoingMatch;
import com.scoreboard.model.entity.Player;

public class MatchResultMapper {

    public MatchResult map(OngoingMatch ongoingMatch) {
        Player winner = ongoingMatch.getWinner();
        Player player1 = ongoingMatch.getFirstPlayer();
        Player player2 = ongoingMatch.getSecondPlayer();

        return MatchResult.builder()
                .winnerName(winner.getName())
                .firstPlayerName(player1.getName())
                .secondPlayerName(player2.getName())
                .firstPlayerRowClass(winner.equals(player1) ? "winner" : "")
                .secondPlayerRowClass(winner.equals(player2) ? "winner" : "")
                .build();
    }
}
