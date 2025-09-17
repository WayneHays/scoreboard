package com.scoreboard.util;

import com.scoreboard.model.Match;
import com.scoreboard.model.Player;

public final class MatchUtils {

    private MatchUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static Player findPlayerInMatch(Match match, String playerId) {
        if (playerId.equals(match.getFirstPlayer().getId().toString())) {
            return match.getFirstPlayer();
        }
        if (playerId.equals(match.getSecondPlayer().getId().toString())) {
            return match.getSecondPlayer();
        }
        throw new IllegalArgumentException("Player with ID " + playerId + " not found in this match");
    }
}
