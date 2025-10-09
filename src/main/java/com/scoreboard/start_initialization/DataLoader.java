package com.scoreboard.start_initialization;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.service.FinishedMatchesService;
import com.scoreboard.service.PlayerService;
import com.scoreboard.start_initialization.data_source.DataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataLoader {

    public static void loadData(DataSource dataSource) {
        PlayerService playerService = ApplicationContext.get(PlayerService.class);
        FinishedMatchesService finishedMatchesService = ApplicationContext.get(FinishedMatchesService.class);

        List<Player> players = createPlayers(playerService, dataSource);
        createMatches(finishedMatchesService, players);
    }

    private static List<Player> createPlayers(PlayerService playerService, DataSource dataSource) {
        List<Player> players = new ArrayList<>();

        for (String playerName : dataSource.getPlayers()) {
            players.add(playerService.create(playerName));
        }
        return players;
    }

    private static void createMatches(FinishedMatchesService finishedMatchesService, List<Player> players) {
        for (int i = 0; i < players.size() - 1; i++) {
            Player player1 = players.get(i);
            Player player2 = players.get(i + 1);
            Player winner = (i % 2 == 0) ? player1 : player2;
            Match match = new Match(player1, player2, winner);

            finishedMatchesService.saveToDatabase(match);
        }
    }
}
