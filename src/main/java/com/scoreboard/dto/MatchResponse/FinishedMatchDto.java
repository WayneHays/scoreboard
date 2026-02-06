package com.scoreboard.dto.MatchResponse;

public record FinishedMatchDto(
        String firstPlayerName,
        String secondPlayerName,
        String winnerName
) {
}
