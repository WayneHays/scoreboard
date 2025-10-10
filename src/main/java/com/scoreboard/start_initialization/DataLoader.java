package com.scoreboard.start_initialization;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.model.OngoingMatch;
import com.scoreboard.model.Score;
import com.scoreboard.model.entity.Match;
import com.scoreboard.model.entity.Player;
import com.scoreboard.service.FinishedMatchPersistenceService;
import com.scoreboard.start_initialization.data_source.DataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataLoader {

    public static void loadData(DataSource dataSource) {
        FinishedMatchPersistenceService finishedMatchPersistenceService = ApplicationContext.get(FinishedMatchPersistenceService.class);

        List<Player> players = createPlayers(dataSource);
        createMatches(finishedMatchPersistenceService, players);
    }

    private static List<Player> createPlayers(DataSource dataSource) {
        List<Player> players = new ArrayList<>();

        for (String playerName : dataSource.getPlayerNames()) {
            players.add(new Player(playerName));
        }
        return players;
    }

    private static void createMatches(FinishedMatchPersistenceService finishedMatchPersistenceService, List<Player> players) {
        for (int i = 0; i < players.size() - 1; i++) {
            Player player1 = players.get(i);
            Player player2 = players.get(i + 1);
            Player winner = (i % 2 == 0) ? player1 : player2;
            OngoingMatch ongoingMatch = new OngoingMatch(
                    new Match(player1, player2, winner),
                    new Score(player1, player2));

            finishedMatchPersistenceService.saveFinishedMatch(ongoingMatch);
        }
    }
}
