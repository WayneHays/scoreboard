package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.model.Match;
import com.scoreboard.model.Player;

import java.util.List;

public class FindMatchesService extends BaseTransactionalService {
    private static final FindMatchesService INSTANCE = new FindMatchesService();
    private final MatchDao matchDao = MatchDao.getInstance();

    public static FindMatchesService getInstance() {
        return INSTANCE;
    }

    public int getTotalCountOfPages() {
        return executeInTransaction(
                matchDao::getTotalCountOfPages,
                "Failed to get total count of pages");
    }

    public int getTotalCountOfPagesByPlayer(Player player) {
        return executeInTransaction(
                () -> matchDao.getTotalCountOfPagesByPlayer(player),
                "Failed to get total count of pages by player");
    }

    public List<Match> findMatchesByPage(int pageNumber) {
        return executeInTransaction(
                () -> matchDao.find(pageNumber),
                "Failed to get matches by page"
        );
    }

    public List<Match> findMatchesByPlayerByPage(Player player, int pageNumber) {
        return executeInTransaction(
                () -> matchDao.find(player, pageNumber),
                "Failed to get matches by player and page"
        );
    }
}
