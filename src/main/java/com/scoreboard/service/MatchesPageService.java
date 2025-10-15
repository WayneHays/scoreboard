package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.dto.MatchesPage;
import com.scoreboard.exception.NotFoundException;
import com.scoreboard.mapper.MatchesPageMapper;
import com.scoreboard.model.entity.Match;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class MatchesPageService extends BaseTransactionalService {
    private final MatchDao matchDao;
    private final MatchesPageMapper mapper;

    public MatchesPage getMatchesPage(int pageNumber, int matchesPerPage) {
        return buildMatchesPage(
                pageNumber,
                matchesPerPage,
                null,
                matchDao::getTotalCountOfMatches,
                () -> matchDao.find(pageNumber, matchesPerPage)
        );
    }

    public MatchesPage getMatchesPageByPlayerName(String playerName, int pageNumber, int matchesPerPage) {
        return buildMatchesPage(
                pageNumber,
                matchesPerPage,
                playerName,
                () -> matchDao.getTotalCountOfMatchesByPlayerName(playerName),
                () -> matchDao.findByPlayerName(playerName, pageNumber, matchesPerPage)
        );
    }

    private MatchesPage buildMatchesPage(
            int pageNumber,
            int matchesPerPage,
            String filter,
            Supplier<Long> getTotalCount,
            Supplier<List<Match>> getMatches
    ) {
        long totalMatches = executeInTransaction(getTotalCount, "Failed to get total count");
        long totalPages = calculateTotalPages(totalMatches, matchesPerPage);

        if (totalPages == 0) {
            return mapper.map(pageNumber, Collections.emptyList(), 0, filter);
        }

        validatePageNumber(pageNumber, totalPages);

        List<Match> matches = executeInTransaction(getMatches, "Failed to get matches");

        return mapper.map(pageNumber, matches, totalPages, filter);
    }

    private int calculateTotalPages(long totalMatches, int matchesPerPage) {
        if (totalMatches < 1) return 0;
        return (int) ((totalMatches - 1) / matchesPerPage + 1);
    }

    private void validatePageNumber(int pageNumber, long totalPages) {
        if (pageNumber < 1 || (totalPages > 0 && pageNumber > totalPages)) {
            String message = (totalPages == 0)
                    ? "No matches found"
                    : String.format("Page %d not found. Available pages: 1-%d", pageNumber, totalPages);
            throw new NotFoundException(message);
        }
    }
}
