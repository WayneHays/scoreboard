package com.scoreboard.startup;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.startup.data_source.DataSource;
import com.scoreboard.model.entity.Match;
import com.scoreboard.model.entity.Player;
import com.scoreboard.service.FinishedMatchesService;
import com.scoreboard.service.PlayerService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            Optional<Player> existingPlayer = playerService.find(playerName);

            if (existingPlayer.isEmpty()) {
                Player player = playerService.create(playerName);
                players.add(player);
            } else {
                players.add(existingPlayer.get());
            }
        }
        return players;
    }

    private static void createMatches(FinishedMatchesService finishedMatchesService, List<Player> players) {
        for (int i = 0; i < players.size() - 1; i++) {
            Player player1 = players.get(i);
            Player player2 = players.get(i + 1);
            Player winner = (i % 2 == 0) ? player1 : player2;
            Match match = new Match(player1, player2);
            match.setWinner(winner);

            finishedMatchesService.saveToDatabase(match);
        }
    }
}
