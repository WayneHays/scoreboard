package com.scoreboard.mapper;

import com.scoreboard.dto.response.MatchResult;
import com.scoreboard.model.entity.Player;
import com.scoreboard.model.ongoingmatch.OngoingMatch;

public class MatchResultMapper implements Mapper<OngoingMatch, MatchResult> {
    private static final String WINNER_CSS_CLASS = "winner";
    private static final String DEFAULT_CSS_CLASS = "";

    public MatchResult map(OngoingMatch ongoingMatch) {
        Player winner = ongoingMatch.getWinner();
        Player player1 = ongoingMatch.getPlayer1();
        Player player2 = ongoingMatch.getPlayer2();

        return MatchResult.builder()
                .winnerName(winner.getName())
                .firstPlayerName(player1.getName())
                .secondPlayerName(player2.getName())
                .firstPlayerFinalSets(ongoingMatch.getSets(player1))
                .secondPlayerFinalSets(ongoingMatch.getSets(player2))
                .firstPlayerRowClass(getPlayerCssClass(player1, winner))
                .secondPlayerRowClass(getPlayerCssClass(player2, winner))
                .build();
    }

    private String getPlayerCssClass(Player player, Player winner) {
        return winner.equals(player) ? WINNER_CSS_CLASS : DEFAULT_CSS_CLASS;
    }
}
