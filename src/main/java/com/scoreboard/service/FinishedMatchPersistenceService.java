package com.scoreboard.service;

import com.scoreboard.dto.MatchResponse.FinishedMatchDto;
import com.scoreboard.entity.Match;
import com.scoreboard.entity.Player;
import com.scoreboard.persistence.dao.MatchDao;
import com.scoreboard.persistence.dao.PlayerDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class FinishedMatchPersistenceService {
    private static final String LOG_START = "Saving match with players: {}, {}, winner: {}";
    private static final String LOG_SUCCESS = "Saving succeed";

    private final MatchDao matchDao;
    private final PlayerDao playerDao;
    private final BaseTransactionalService baseTransactionalService;

    public void saveFinishedMatch(FinishedMatchDto dto) {
        String firstPlayerName = dto.firstPlayerName();
        String secondPlayerName = dto.secondPlayerName();
        String winnerName = dto.winnerName();

        log.info(LOG_START, firstPlayerName, secondPlayerName, winnerName);

        Set<String> namesFromMatch = Set.of(firstPlayerName, secondPlayerName);

        baseTransactionalService.executeInTransaction(() -> {
            Map<String, Player> existingPlayers = playerDao.findByNames(namesFromMatch).stream()
                    .collect(Collectors.toMap(
                            player -> player.getName().toLowerCase(),
                            player -> player
                    ));

            Player firstPlayer = existingPlayers.getOrDefault(
                    firstPlayerName.toLowerCase(),
                    new Player(firstPlayerName)
            );

            Player secondPlayer = existingPlayers.getOrDefault(
                    secondPlayerName.toLowerCase(),
                    new Player(secondPlayerName)
            );

            Player winner = winnerName.equalsIgnoreCase(firstPlayerName)
                    ? firstPlayer
                    : secondPlayer;

            matchDao.save(new Match(firstPlayer, secondPlayer, winner));
        });

        log.info(LOG_SUCCESS);
    }
}
