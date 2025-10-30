package com.scoreboard.service.finishedmatchpersistenceservice;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.dao.PlayerDao;
import com.scoreboard.model.entity.Match;
import com.scoreboard.model.entity.Player;
import com.scoreboard.model.ongoingmatch.OngoingMatch;
import com.scoreboard.service.BaseTransactionalService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FinishedMatchPersistenceService extends BaseTransactionalService {
    private final MatchDao matchDao;
    private final PlayerDao playerDao;

    public void saveFinishedMatch(OngoingMatch ongoingMatch) {
        executeInTransaction(() -> {
            Player savedPlayer1 = savePlayer(ongoingMatch.getPlayer1());
            Player savedPlayer2 = savePlayer(ongoingMatch.getPlayer2());
            Player savedWinner = savePlayer(ongoingMatch.getWinner());

            matchDao.save(new Match(savedPlayer1, savedPlayer2, savedWinner));
        }, "Failed to save finished match");
    }

    private Player savePlayer(Player player) {
        return playerDao.find(player.getName())
                .orElseGet(() -> {
                    try {
                        playerDao.save(player);
                        return player;
                    } catch (Exception e) {
                        return playerDao.find(player.getName())
                                .orElseThrow(() -> new RuntimeException(
                                        "Failed to save player: " + player.getName(), e
                                ));
                    }
                });
    }
}
