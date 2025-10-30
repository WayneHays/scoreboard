package com.scoreboard.dto.response;

import lombok.Builder;

@Builder
public record MatchLiveView(
        String firstPlayerName,
        String secondPlayerName,
        String firstPlayerId,
        String secondPlayerId,
        int firstPlayerSets,
        int secondPlayerSets,
        int firstPlayerGames,
        int secondPlayerGames,
        String firstPlayerPoints,
        String secondPlayerPoints
) {}