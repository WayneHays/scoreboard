package com.scoreboard.dto.MatchResponse;

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
        boolean isTieBreak,
        boolean isFinished
) {
}
