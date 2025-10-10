package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.dto.MatchesPage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MatchQueryService extends BaseTransactionalService {
    private final MatchDao matchDao;
    private final PaginationService paginationService;

    public MatchesPage getMatchesPage(int pageNumber, int matchesPerPage) {
        return paginationService.buildPage(
                pageNumber,
                matchesPerPage,
                null,
                matchDao::getTotalCountOfMatches,
                () -> matchDao.find(pageNumber, matchesPerPage)
        );
    }

    public MatchesPage getMatchesPageByPlayerName(String playerName, int pageNumber, int matchesPerPage) {
        return paginationService.buildPage(
                pageNumber,
                matchesPerPage,
                playerName,
                () -> matchDao.getTotalCountOfMatchesByPlayerName(playerName),
                () -> matchDao.findByPlayerName(playerName, pageNumber, matchesPerPage)
        );
    }
}
