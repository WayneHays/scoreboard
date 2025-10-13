package com.scoreboard.dto;

import com.scoreboard.model.entity.Match;
import lombok.Builder;

import java.util.List;

@Builder
public record MatchesPage(
        long pageNumber,
        List<Match> matches,
        long totalPages,
        String filterByPlayerName,
        boolean hasFilter,
        boolean shouldShowPagination,
        boolean hasPrevious,
        boolean hasNext,
        long previousPage,
        long nextPage,
        String validationError,
        String invalidPlayerName) {

    public boolean hasMatches() {
        return matches != null && !matches.isEmpty();
    }

    public boolean hasValidationError() {
        return validationError != null;
    }

    public MatchesPage withValidationError(String error, String invalidName) {
        return MatchesPage.builder()
                .pageNumber(this.pageNumber)
                .matches(this.matches)
                .totalPages(this.totalPages)
                .filterByPlayerName(null)
                .hasFilter(false)
                .shouldShowPagination(this.shouldShowPagination)
                .hasPrevious(this.hasPrevious)
                .hasNext(this.hasNext)
                .previousPage(this.previousPage)
                .nextPage(this.nextPage)
                .validationError(error)
                .invalidPlayerName(invalidName)
                .build();
    }
}
