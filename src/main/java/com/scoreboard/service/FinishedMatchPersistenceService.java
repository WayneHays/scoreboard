package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.dao.PlayerDao;
import com.scoreboard.dto.FinishedMatchDto;
import com.scoreboard.entity.Match;
import com.scoreboard.entity.Player;
import com.scoreboard.exception.DaoException;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class FinishedMatchPersistenceService {
    private final MatchDao matchDao;
    private final PlayerDao playerDao;
    private final BaseTransactionalService baseTransactionalService;

    public void saveFinishedMatch(FinishedMatchDto dto) {
        String firstPlayerName = dto.firstPlayerName();
        String secondPlayerName = dto.secondPlayerName();
        Set<String> namesFromMatch = Set.of(firstPlayerName, secondPlayerName);

        baseTransactionalService.executeInTransaction(() -> {
            Map<String, Player> players = playerDao.findByNames(namesFromMatch).stream()
                    .collect(Collectors.toMap(
                            player -> player.getName().toLowerCase(),
                            player -> player
                    ));

            Player firstPlayer = players.computeIfAbsent(firstPlayerName.toLowerCase(), this::createPlayer);
            Player secondPlayer = players.computeIfAbsent(secondPlayerName.toLowerCase(), this::createPlayer);

            Player winner = dto.winnerName().equalsIgnoreCase(firstPlayerName) ? firstPlayer : secondPlayer;
            matchDao.save(new Match(firstPlayer, secondPlayer, winner));
        });
    }

    private Player createPlayer(String name) {
        Player player = new Player(name);
        try {
            return playerDao.save(player);
        } catch (Exception e) {
            if (isConstraintViolation(e)) {
                return playerDao.find(name)
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

    private DaoException getException(Player player, Exception e) {
        return new DaoException("Failed to save player: " + player.getName(), e);
    }
}
