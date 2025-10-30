package com.scoreboard.dto.response;

import lombok.Builder;

@Builder
public record MatchResult(
        String winnerName,
        String firstPlayerName,
        String secondPlayerName,
        int firstPlayerFinalSets,
        int secondPlayerFinalSets,
        String firstPlayerRowClass,
        String secondPlayerRowClass
) {}
