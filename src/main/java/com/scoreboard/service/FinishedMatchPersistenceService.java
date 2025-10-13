package com.scoreboard.service;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.dao.MatchDao;
import com.scoreboard.dao.PlayerDao;
import com.scoreboard.model.OngoingMatch;
import com.scoreboard.model.entity.Player;

public class FinishedMatchPersistenceService extends BaseTransactionalService {
    private final MatchDao matchDao;
    private final PlayerDao playerDao;

    public FinishedMatchPersistenceService() {
        this.matchDao = ApplicationContext.get(MatchDao.class);
        this.playerDao = ApplicationContext.get(PlayerDao.class);
    }

    public void saveFinishedMatch(OngoingMatch ongoingMatch) {
        executeInTransaction(() -> ongoingMatch.withMatch(match -> {
            Player player1FromDb = ensurePlayerInDatabase(match.getFirstPlayer());
            Player player2FromDb = ensurePlayerInDatabase(match.getSecondPlayer());
            match.setPlayers(player1FromDb, player2FromDb);

            Player winnerFromDb = determineWinnerFromDb(
                    match.getWinner(), player1FromDb, player2FromDb
            );
            match.setWinner(winnerFromDb);

            matchDao.save(match);
        }), "Failed to save finished match");
    }

    private Player ensurePlayerInDatabase(Player player) {
        if (player.getId() != null) {
            return player;
        }

        return playerDao.find(player.getName())
                .orElseGet(() -> savePlayer(player));
    }

    private Player savePlayer(Player player) {
        try {
            playerDao.save(player);
            return player;
        } catch (Exception e) {
            return playerDao.find(player.getName())
                    .orElseThrow(() -> new RuntimeException(
                            "Failed to save or find player: " + player.getName(), e
                    ));
        }
    }

    private Player determineWinnerFromDb(Player winner, Player player1FromDb, Player player2FromDb) {
        if (winner.getName().equals(player1FromDb.getName())) {
            return player1FromDb;
        }
        if (winner.getName().equals(player2FromDb.getName())) {
            return player2FromDb;
        }
        throw new IllegalStateException("Winner is not among match players");
    }
}
