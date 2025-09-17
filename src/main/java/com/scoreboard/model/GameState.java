package com.scoreboard.model;

public record GameState(
        Score score,
        boolean isTieBreak,
        Player advantagePlayer
) {
}

