package com.scoreboard.mapper;

import com.scoreboard.config.ServiceProvider;
import com.scoreboard.dto.MatchesPage;
import com.scoreboard.model.entity.Match;

import java.util.List;

public class MatchesPageMapper implements ServiceProvider {

    public MatchesPage map(long pageNumber, List<Match> matches,
                           long totalPages, String filterByPlayerName) {
        boolean hasFilter = filterByPlayerName != null && !filterByPlayerName.trim().isEmpty();
        boolean shouldShowPagination = totalPages > 1;
        boolean hasPrevious = pageNumber > 1;
        boolean hasNext = pageNumber < totalPages;
        long previousPage = hasPrevious ? pageNumber - 1 : 1;
        long nextPage = hasNext ? pageNumber + 1 : Math.max(1, totalPages);

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
