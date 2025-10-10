package com.scoreboard.service;

import com.scoreboard.dto.MatchesPage;
import com.scoreboard.exception.NotFoundException;
import com.scoreboard.mapper.MatchesPageMapper;
import com.scoreboard.model.entity.Match;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@AllArgsConstructor
public class PaginationService extends BaseTransactionalService {
    private final MatchesPageMapper mapper;

    public MatchesPage buildPage(
            int pageNumber,
            int itemsPerPage,
            String filter,
            Supplier<Integer> getTotalCount,
            Supplier<List<Match>> getItems
    ) {
        int totalItems = executeInTransaction(getTotalCount, "Failed to get total count");
        int totalPages = calculateTotalPages(totalItems, itemsPerPage);

        if (totalPages == 0) {
            return mapper.map(pageNumber, Collections.emptyList(), 0, filter);
        }

        validatePageNumber(pageNumber, totalPages);

        List<Match> items = executeInTransaction(getItems, "Failed to get items");

        return mapper.map(pageNumber, items, totalPages, filter);
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
