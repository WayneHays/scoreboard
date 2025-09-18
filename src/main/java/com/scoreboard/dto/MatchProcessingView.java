package com.scoreboard.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MatchProcessingView {
    private final String firstPlayerName;
    private final String secondPlayerName;
    private final String firstPlayerId;
    private final String secondPlayerId;
    private final int firstPlayerSets;
    private final int secondPlayerSets;
    private final int firstPlayerGames;
    private final int secondPlayerGames;
    private final String firstPlayerPoints;
    private final String secondPlayerPoints;
}