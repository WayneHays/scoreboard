package com.scoreboard.initialization.database;

import com.scoreboard.exception.ApplicationStartupException;
import com.scoreboard.model.entity.Player;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.service.finishedmatchpersistence.FinishedMatchPersistenceService;
import com.scoreboard.initialization.factory.TestMatchFactory;
import com.scoreboard.initialization.datasource.DataSource;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class DatabaseInitializerImpl implements DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializerImpl.class);
    private static final int MIN_PLAYERS_TO_CREATE_MATCH = 2;

    private final DataSource dataSource;
    private final FinishedMatchPersistenceService finishedMatchPersistenceService;
    private final TestMatchFactory testMatchFactory;

    public void initialize() {
        logger.info("Starting database initialization...");

        try {
            List<Player> players = loadPlayers();
            List<OngoingMatch> matches = testMatchFactory.createMatches(players);
            saveMatches(matches);

            logger.info("Database initialization completed: {} players, {} matches",
                    players.size(), matches.size());

        } catch (ApplicationStartupException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Database initialization failed", e);
            throw new ApplicationStartupException("Database initialization failed", e);
        }
    }

    private List<Player> loadPlayers() {
        logger.debug("Loading player names from data source");

        List<String> playerNames = dataSource.getPlayerNames();

        if (playerNames.isEmpty()) {
            throw new ApplicationStartupException("No player names found in data source");
        }

        Set<String> uniqueNames = new LinkedHashSet<>(playerNames);

        if (uniqueNames.size() != playerNames.size()) {
            logger.warn("Removed {} duplicate player names",
                    playerNames.size() - uniqueNames.size());
        }

        List<Player> players = uniqueNames.stream()
                .map(Player::new)
                .toList();

        if (players.size() < MIN_PLAYERS_TO_CREATE_MATCH) {
            throw new ApplicationStartupException(
                    String.format("Not enough players to initialize. Required: %d, Found: %d",
                            MIN_PLAYERS_TO_CREATE_MATCH, players.size())
            );
        }

        logger.info("Loaded {} players from data source", players.size());
        return players;
    }

    private void saveMatches(List<OngoingMatch> matches) {
        if (matches.isEmpty()) {
            logger.warn("No matches to save");
            return;
        }

        logger.debug("Saving {} matches to database", matches.size());

        for (OngoingMatch ongoingMatch : matches) {
            finishedMatchPersistenceService.saveFinishedMatch(ongoingMatch);
        }

        logger.debug("Successfully saved {} matches", matches.size());
    }
}
