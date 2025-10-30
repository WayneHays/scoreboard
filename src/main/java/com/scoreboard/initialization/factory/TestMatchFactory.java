package com.scoreboard.initialization.factory;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.ongoingmatch.OngoingMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TestMatchFactory {
    private static final Logger logger = LoggerFactory.getLogger(TestMatchFactory.class);
    private static final int MIN_PLAYERS_TO_CREATE_MATCH = 2;

    public List<OngoingMatch> createMatches(List<Player> players) {
        if (players == null || players.size() < MIN_PLAYERS_TO_CREATE_MATCH) {
            throw new IllegalArgumentException(
                    "Cannot create matches: need at least 2 players, got " +
                    (players == null ? "null" : players.size())
            );
        }

        logger.debug("Creating demo matches from {} players", players.size());

        List<OngoingMatch> finishedMatches = new ArrayList<>();

        for (int i = 0; i < players.size() - 1; i++) {
            Player player1 = players.get(i);
            Player player2 = players.get(i + 1);

            Player winner = (i % 2 == 0) ? player1 : player2;

            OngoingMatch match = createMatch(player1, player2, winner);
            finishedMatches.add(match);
        }

        logger.info("Created {} demo matches", finishedMatches.size());
        return finishedMatches;
    }

    private OngoingMatch createMatch(Player player1, Player player2, Player winner) {
        validatePlayers(player1, player2, winner);

        OngoingMatch match = new OngoingMatch(player1, player2);
        match.setWinner(winner);

        return match;
    }

    private void validatePlayers(Player player1, Player player2, Player winner) {
        if (player1 == null || player2 == null || winner == null) {
            throw new IllegalArgumentException("Players and winner cannot be null");
        }

        if (!winner.equals(player1) && !winner.equals(player2)) {
            throw new IllegalArgumentException(
                    String.format("Winner '%s' must be one of the players: '%s' or '%s'",
                            winner.getName(), player1.getName(), player2.getName())
            );
        }
    }
}
