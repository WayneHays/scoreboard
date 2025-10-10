package com.scoreboard.mapper;

import com.scoreboard.dto.MatchLiveView;
import com.scoreboard.model.OngoingMatch;
import com.scoreboard.model.entity.Player;

public class MatchLiveViewMapper {
    private static final String ADVANTAGE_VIEW = "AD";

    public MatchLiveView map(OngoingMatch ongoingMatch) {
        Player player1 = ongoingMatch.getFirstPlayer();
        Player player2 = ongoingMatch.getSecondPlayer();
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
        if (playerHasAdvantage(ongoingMatch, player)) {
            return ADVANTAGE_VIEW;
        }

        if (ongoingMatch.isTieBreak()) {
            return String.valueOf(ongoingMatch.getTieBreakPoints(player));
        }

        return String.valueOf(ongoingMatch.getPoints(player));
    }

    private boolean playerHasAdvantage(OngoingMatch ongoingMatch, Player player) {
        return ongoingMatch.getAdvantageStatus() != null && ongoingMatch.getAdvantageStatus().equals(player);
    }
}
