package com.scoreboard.start_initialization;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.config.ServiceProvider;
import com.scoreboard.model.OngoingMatch;
import com.scoreboard.model.Score;
import com.scoreboard.model.entity.Match;
import com.scoreboard.model.entity.Player;
import com.scoreboard.service.FinishedMatchPersistenceService;
import com.scoreboard.start_initialization.data_source.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StartupDatabaseInitializer implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(StartupDatabaseInitializer.class);

    private final DataSource dataSource;
    private final FinishedMatchPersistenceService finishedMatchPersistenceService;

    public StartupDatabaseInitializer(DataSource dataSource,
                                      FinishedMatchPersistenceService finishedMatchPersistenceService) {
        this.dataSource = dataSource;
        this.finishedMatchPersistenceService = finishedMatchPersistenceService;
    }

    @Override
    public Class<?> getServiceType() {
        return StartupDatabaseInitializer.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new StartupDatabaseInitializer(
                context.get(DataSource.class),
                context.get(FinishedMatchPersistenceService.class)
        );
    }

    public void initialize() {
        logger.info("Starting database initialization...");

        try {
            List<Player> players = createPlayers();
            List<OngoingMatch> matches = createMatches(players);
            saveMatchesToDb(matches);

            logger.info("Database initialization completed successfully. Created {} players and {} matches",
                    players.size(), players.size() - 1);
        } catch (Exception e) {
            logger.error("Database initialization failed", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private List<Player> createPlayers() {
        logger.debug("Creating players from data source");
        List<Player> players = dataSource.getPlayerNames().stream()
                .map(Player::new)
                .toList();

        logger.debug("Created {} players", players.size());
        return players;
    }

    private List<OngoingMatch> createMatches(List<Player> players) {
        logger.debug("Creating matches from {} players", players.size());
        List<OngoingMatch> matches = new ArrayList<>();

        for (int i = 0; i < players.size() - 1; i++) {
            Player player1 = players.get(i);
            Player player2 = players.get(i + 1);
            Player winner = (i % 2 == 0) ? player1 : player2;

            OngoingMatch ongoingMatch = new OngoingMatch(
                    new Match(player1, player2, winner),
                    new Score(player1, player2)
            );
            matches.add(ongoingMatch);
        }
        logger.debug("Created {} matches", matches.size());
        return matches;
    }

    public void saveMatchesToDb(List<OngoingMatch> matches) {
        for (OngoingMatch ongoingMatch : matches) {
            finishedMatchPersistenceService.saveFinishedMatch(ongoingMatch);

        }
    }
}
