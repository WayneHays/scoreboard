package com.scoreboard.mapper;

import com.scoreboard.dto.MatchesPage;
import com.scoreboard.model.Match;

import java.util.List;

public class MatchesPageMapper {

    public MatchesPage map(int pageNumber, List<Match> matches,
                           int totalPages, String filterByPlayerName) {
        boolean hasFilter = filterByPlayerName != null && !filterByPlayerName.trim().isEmpty();
        boolean shouldShowPagination = totalPages > 1;
        boolean hasPrevious = pageNumber > 1;
        boolean hasNext = pageNumber < totalPages;
        int previousPage = hasPrevious ? pageNumber - 1 : 1;
        int nextPage = hasNext ? pageNumber + 1 : Math.max(1, totalPages);

        return MatchesPage.builder()
                .pageNumber(pageNumber)
                .matches(matches)
                .totalPages(totalPages)
                .filterByPlayerName(filterByPlayerName)
                .hasFilter(hasFilter)
                .shouldShowPagination(shouldShowPagination)
                .hasPrevious(hasPrevious)
                .hasNext(hasNext)
                .previousPage(previousPage)
                .nextPage(nextPage)
                .build();
    }
}
