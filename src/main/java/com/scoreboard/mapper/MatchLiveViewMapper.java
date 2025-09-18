package com.scoreboard.mapper;

import com.scoreboard.dto.MatchLiveView;
import com.scoreboard.dto.OngoingMatch;
import com.scoreboard.model.Player;

public class MatchLiveViewMapper {
    private static final String ADVANTAGE_VIEW = "AD";

    public MatchLiveView map(OngoingMatch ongoingMatch) {
        return MatchLiveView.builder()
                .firstPlayerName(ongoingMatch.getMatch().getFirstPlayer().getName())
                .secondPlayerName(ongoingMatch.getMatch().getSecondPlayer().getName())
                .firstPlayerId(String.valueOf(ongoingMatch.getMatch().getFirstPlayer().getId()))
                .secondPlayerId(String.valueOf(ongoingMatch.getMatch().getSecondPlayer().getId()))
                .firstPlayerSets(ongoingMatch.getScore().getSets(ongoingMatch.getMatch().getFirstPlayer()))
                .secondPlayerSets(ongoingMatch.getScore().getSets(ongoingMatch.getMatch().getSecondPlayer()))
                .firstPlayerGames(ongoingMatch.getScore().getGames(ongoingMatch.getMatch().getFirstPlayer()))
                .secondPlayerGames(ongoingMatch.getScore().getGames(ongoingMatch.getMatch().getSecondPlayer()))
                .firstPlayerPoints(formatPlayerPoints(ongoingMatch, ongoingMatch.getMatch().getFirstPlayer()))
                .secondPlayerPoints(formatPlayerPoints(ongoingMatch, ongoingMatch.getMatch().getSecondPlayer()))
                .build();
    }

    private String formatPlayerPoints(OngoingMatch match, Player player) {
        if (match.getAdvantage() != null &&
            match.getAdvantage().equals(player)) {
            return ADVANTAGE_VIEW;
        }

        if (match.isTieBreak()) {
            return String.valueOf(match.getScore().getTieBreakPoints(player));
        }

        return String.valueOf(match.getScore().getPoints(player));
    }
}
