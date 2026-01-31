package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.dao.PlayerDao;
import com.scoreboard.exception.ScoreboardServiceException;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.model.entity.Match;
import com.scoreboard.model.entity.Player;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Optional;

@AllArgsConstructor
public class FinishedMatchPersistenceService {
    private final MatchDao matchDao;
    private final PlayerDao playerDao;
    private final BaseTransactionalService baseTransactionalService;

    public void saveFinishedMatch(OngoingMatch ongoingMatch) {
        String firstPlayerName = ongoingMatch.getFirstPlayerName().toLowerCase();
        String secondPlayerName = ongoingMatch.getSecondPlayerName().toLowerCase();

        baseTransactionalService.executeInTransaction(() -> {
            Player firstPlayer = findOrCreatePlayer(firstPlayerName);
            Player secondPlayer = findOrCreatePlayer(secondPlayerName);
            String winnerName = ongoingMatch.getWinnerName()
                    .orElseThrow(() -> new IllegalStateException("Match is finished, winner should be set"));
            Player winner = winnerName.equalsIgnoreCase(firstPlayerName) ? firstPlayer : secondPlayer;

            matchDao.save(new Match(firstPlayer, secondPlayer, winner));
        });
    }

    private Player findOrCreatePlayer(String name) {
        return tryFind(name)
                .orElseGet(() -> trySave(name));
    }

    private Optional<Player> tryFind(String name) {
        return playerDao.find(name);
    }

    private Player trySave(String name) {
        Player player = new Player(name);
        try {
            return playerDao.save(player);
        } catch (Exception e) {
            if (isConstraintViolation(e)) {
                return tryFind(name)
                        .orElseThrow(() -> getException(player, e));
            }
            throw getException(player, e);
        }
    }

    private boolean isConstraintViolation(Exception e) {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof ConstraintViolationException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private ScoreboardServiceException getException(Player player, Exception e) {
        return new ScoreboardServiceException("Failed to save player: " + player.getName(), e);
    }
}
