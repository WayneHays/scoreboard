package com.scoreboard.service;

import com.scoreboard.config.AppConfig;
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
        int totalMatches = executeInTransaction(
                matchDao::getTotalCountOfMatches,
                "Failed to get total count of matches");
        return calculateTotalPages(totalMatches);
    }

    public int getTotalCountOfPagesByPlayer(Player player) {
        int totalMatches = executeInTransaction(
                () -> matchDao.getTotalCountOfMatchesByPlayer(player),
                "Failed to get total count of matches by player");
        return calculateTotalPages(totalMatches);
    }

    public List<Match> findMatchesByPage(int pageNumber) {
        return executeInTransaction(
                () -> matchDao.find(pageNumber),
                "Failed to get matches by page");
    }

    public List<Match> findMatchesByPlayerByPage(Player player, int pageNumber) {
        return executeInTransaction(
                () -> matchDao.find(player, pageNumber),
                "Failed to get matches by player and page");
    }

    private int calculateTotalPages(int totalRecords) {
        return totalRecords == 0 ? 0 : (totalRecords - 1) / AppConfig.PAGE_SIZE + 1;
    }
}
