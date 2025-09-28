package com.scoreboard.service;

import com.scoreboard.config.ApplicationConfig;
import com.scoreboard.dao.MatchDao;
import com.scoreboard.model.entity.Match;
import com.scoreboard.model.entity.Player;
import com.scoreboard.util.PaginationHelper;

import java.util.List;

public class FindMatchesService extends BaseTransactionalService {
    private final MatchDao matchDao;

    public FindMatchesService(MatchDao matchDao) {
        this.matchDao = matchDao;
    }

    public int getTotalCountOfPages() {
        return executeInTransaction(
                () -> PaginationHelper.calculateTotalPages(
                        matchDao.getTotalCountOfMatches()),
                "Failed to get total count of pages");
    }

    public int getTotalCountOfPagesByPlayer(Player player) {
        return executeInTransaction(
                () -> PaginationHelper.calculateTotalPages(
                        matchDao.getTotalCountOfMatchesByPlayer(player)),
                "Failed to get total count of pages by player");
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

    public List<Match> findAllMatchesByPlayer(Player player) {
        return executeInTransaction(
                () -> matchDao.findAllByPlayer(player),
                "Failed to get all matches by player");
    }
}
