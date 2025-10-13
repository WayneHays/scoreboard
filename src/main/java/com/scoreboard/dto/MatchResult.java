package com.scoreboard.dto;

import lombok.Builder;

@Builder
public record MatchResult(
        String winnerName,
        String firstPlayerName,
        String secondPlayerName,
        String firstPlayerRowClass,
        String secondPlayerRowClass
) {
}
