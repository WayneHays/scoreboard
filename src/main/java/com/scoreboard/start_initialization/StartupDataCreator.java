package com.scoreboard.start_initialization;

import com.scoreboard.model.OngoingMatch;
import com.scoreboard.model.Score;
import com.scoreboard.model.entity.Match;
import com.scoreboard.model.entity.Player;
import com.scoreboard.service.FinishedMatchPersistenceService;
import com.scoreboard.start_initialization.data_source.DataSource;

import java.util.List;

public class StartupDataCreator {
    private final FinishedMatchPersistenceService finishedMatchPersistenceService;

    public StartupDataCreator(FinishedMatchPersistenceService finishedMatchPersistenceService) {
        this.finishedMatchPersistenceService = finishedMatchPersistenceService;
    }

    public void createInitialData(DataSource dataSource) {
        List<Player> players = createPlayers(dataSource);
        createMatches(players);
    }

    private List<Player> createPlayers(DataSource dataSource) {
        return dataSource.getPlayerNames().stream()
                .map(Player::new)
                .toList();
    }

    private void createMatches(List<Player> players) {
        for (int i = 0; i < players.size() - 1; i++) {
            Player player1 = players.get(i);
            Player player2 = players.get(i + 1);
            Player winner = (i % 2 == 0) ? player1 : player2;

            OngoingMatch ongoingMatch = new OngoingMatch(
                    new Match(player1, player2, winner),
                    new Score(player1, player2)
            );

            finishedMatchPersistenceService.saveFinishedMatch(ongoingMatch);
        }
    }
}
