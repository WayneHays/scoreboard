package com.scoreboard.dto;

import com.scoreboard.model.GameState;
import com.scoreboard.model.Match;
import com.scoreboard.model.Score;

import java.util.UUID;

public record OngoingMatch(
        Match match,
        GameState gameState,
        UUID uuid) {

    public static OngoingMatch createNew(Match match, Score score, UUID uuid) {
        GameState initialState = new GameState(score, false, null);
        return new OngoingMatch(match, initialState, uuid);
    }
}
