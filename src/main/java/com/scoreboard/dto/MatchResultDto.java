package com.scoreboard.dto;

import lombok.Builder;

@Builder
public record MatchResultDto(
        String winnerName,
        String firstPlayerName,
        String secondPlayerName,
        int firstPlayerSets,
        int secondPlayerSets
) {}
