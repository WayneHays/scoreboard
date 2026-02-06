package com.scoreboard.dto;

public record OngoingMatchDto(
        String firstPlayerName,
        String secondPlayerName,
        String firstPlayerPoints,
        String secondPlayerPoints,
        int firstPlayerGames,
        int secondPlayerGames,
        int firstPlayerTieBreakPoints,
        int secondPlayerTieBreakPoints,
        int firstPlayerSets,
        int secondPlayerSets,
        Boolean isTieBreak,
        Boolean isFinished
) {
}
