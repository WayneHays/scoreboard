package com.scoreboard.dto;

public record FinishedMatchDto(
        String firstPlayerName,
        String secondPlayerName,
        String winnerName
) {
}
