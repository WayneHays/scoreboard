package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.dto.MatchesPage;
import com.scoreboard.exception.NotFoundException;
import com.scoreboard.mapper.MatchesPageMapper;
import com.scoreboard.model.Match;

import java.util.Collections;
import java.util.List;

public class MatchesPageService extends BaseTransactionalService {
    private final MatchDao matchDao;
    private final MatchesPageMapper mapper;

    public MatchesPageService(MatchDao matchDao,
                              MatchesPageMapper mapper) {
        this.matchDao = matchDao;
        this.mapper = mapper;
    }

    public MatchesPage getMatchesPage(int pageNumber, int matchesPerPage) {
        int totalMatches = executeInTransaction(
                matchDao::getTotalCountOfMatches,
                "Failed to get total count of matches"
        );

        int totalPages = calculateTotalPages(totalMatches, matchesPerPage);

        if (totalPages == 0) {
            return mapper.map(pageNumber, Collections.emptyList(), 0, null);
        }

        validatePageNumber(pageNumber, totalPages);

        List<Match> matches = executeInTransaction(
                () -> matchDao.find(pageNumber, matchesPerPage),
                "Failed to find matches by page " + pageNumber
        );

        return mapper.map(pageNumber, matches, totalPages, null);
    }

    public MatchesPage getMatchesPageByPlayerName(String name, int pageNumber, int matchesPerPage) {
        int totalMatches = executeInTransaction(
                () -> matchDao.getTotalCountOfMatchesByPlayerName(name),
                "Failed to get total count of matches by player name " + name
        );

        int totalPages = calculateTotalPages(totalMatches, matchesPerPage);

        if (totalPages == 0) {
            return mapper.map(pageNumber, Collections.emptyList(), 0, name);
        }

        validatePageNumber(pageNumber, totalPages);

        List<Match> matches = executeInTransaction(
                () -> matchDao.findByPlayerName(name, pageNumber, matchesPerPage),
                "Failed to find matches by player name " + name
        );

        return mapper.map(pageNumber, matches, totalPages, name);
    }

    private int calculateTotalPages(int totalMatches, int matchesPerPage) {
        if (totalMatches < 1) return 0;
        return (totalMatches - 1) / matchesPerPage + 1;
    }

    private void validatePageNumber(int pageNumber, int totalPages) {
        if (pageNumber < 1 || (totalPages > 0 && pageNumber > totalPages)) {
            String message = (totalPages == 0)
                    ? "No matches found"
                    : String.format("Page %d not found. Available pages: 1-%d", pageNumber, totalPages);
            throw new NotFoundException(message);
        }
    }
}
