package com.scoreboard.mapper;

import com.scoreboard.dto.response.MatchLiveView;
import com.scoreboard.model.entity.Player;
import com.scoreboard.model.ongoingmatch.OngoingMatch;
import com.scoreboard.service.scorecalculation.Points;

public class MatchLiveViewMapper implements Mapper<OngoingMatch, MatchLiveView> {

    public MatchLiveView map(OngoingMatch ongoingMatch) {
        Player player1 = ongoingMatch.getPlayer1();
        Player player2 = ongoingMatch.getPlayer2();

        return MatchLiveView.builder()
                .firstPlayerName(player1.getName())
                .secondPlayerName(player2.getName())
                .firstPlayerId(String.valueOf(player1.getId()))
                .secondPlayerId(String.valueOf(player2.getId()))
                .firstPlayerSets(ongoingMatch.getSets(player1))
                .secondPlayerSets(ongoingMatch.getSets(player2))
                .firstPlayerGames(ongoingMatch.getGames(player1))
                .secondPlayerGames(ongoingMatch.getGames(player2))
                .firstPlayerPoints(formatPlayerPoints(ongoingMatch, player1))
                .secondPlayerPoints(formatPlayerPoints(ongoingMatch, player2))
                .build();
    }

    private String formatPlayerPoints(OngoingMatch ongoingMatch, Player player) {
        if (player.equals(ongoingMatch.getAdvantage())) {
            return Points.ADVANTAGE.getValue();
        }

        if (ongoingMatch.isTieBreak()) {
            return String.valueOf(ongoingMatch.getTieBreakPoints(player));
        }

        return ongoingMatch.getPoints(player).getValue();
    }
}
