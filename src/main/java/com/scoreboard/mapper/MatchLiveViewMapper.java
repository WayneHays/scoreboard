package com.scoreboard.mapper;

import com.scoreboard.dto.MatchLiveView;
import com.scoreboard.model.OngoingMatch;
import com.scoreboard.model.Player;

public class MatchLiveViewMapper {
    private static final String ADVANTAGE_VIEW = "AD";

    public MatchLiveView map(OngoingMatch ongoingMatch) {
        Player first = ongoingMatch.getFirstPlayer();
        Player second = ongoingMatch.getSecondPlayer();
        return MatchLiveView.builder()
                .firstPlayerName(first.getName())
                .secondPlayerName(second.getName())
                .firstPlayerId(String.valueOf(first.getId()))
                .secondPlayerId(String.valueOf(second.getId()))
                .firstPlayerSets(ongoingMatch.getSets(first))
                .secondPlayerSets(ongoingMatch.getSets(second))
                .firstPlayerGames(ongoingMatch.getGames(first))
                .secondPlayerGames(ongoingMatch.getGames(second))
                .firstPlayerPoints(formatPlayerPoints(ongoingMatch, first))
                .secondPlayerPoints(formatPlayerPoints(ongoingMatch,second))
                .build();
    }

    private String formatPlayerPoints(OngoingMatch ongoingMatch, Player player) {
        if (ongoingMatch.getAdvantage() != null &&
            ongoingMatch.getAdvantage().equals(player)) {
            return ADVANTAGE_VIEW;
        }

        if (ongoingMatch.isTieBreak()) {
            return String.valueOf(ongoingMatch.getTieBreakPoints(player));
        }

        return String.valueOf(ongoingMatch.getPoints(player));
    }
}
