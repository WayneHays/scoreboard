package com.scoreboard.mapper;

import com.scoreboard.dto.request.MatchesPageContext;
import com.scoreboard.dto.response.MatchesPage;

public class MatchesPageMapper implements Mapper<MatchesPageContext, MatchesPage> {

    @Override
    public MatchesPage map(MatchesPageContext context) {
        boolean hasFilter = context.filterByPlayerName() != null &&
                            !context.filterByPlayerName().trim().isEmpty();
        boolean shouldShowPagination = context.totalPages() > 1;
        boolean hasPrevious = context.pageNumber() > 1;
        boolean hasNext = context.pageNumber() < context.totalPages();

        long previousPage = hasPrevious ? context.pageNumber() - 1 : 1;
        long nextPage = hasNext ? context.pageNumber() + 1 : Math.max(1, context.totalPages());

        return MatchesPage.builder()
                .pageNumber(context.pageNumber())
                .matches(context.matches())
                .totalPages(context.totalPages())
                .filterByPlayerName(context.filterByPlayerName())
                .hasFilter(hasFilter)
                .shouldShowPagination(shouldShowPagination)
                .hasPrevious(hasPrevious)
                .hasNext(hasNext)
                .previousPage(previousPage)
                .nextPage(nextPage)
                .build();
    }
}
