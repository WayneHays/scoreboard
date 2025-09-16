package com.scoreboard.dto;

import com.scoreboard.model.Player;
import com.scoreboard.model.Score;

public record GameState(
        Score score,
        boolean isTieBreak,
        Player advantagePlayer
) {
}

