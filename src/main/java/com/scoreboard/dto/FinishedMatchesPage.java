package com.scoreboard.dto;

import com.scoreboard.model.entity.Match;
import lombok.Builder;

import java.util.List;

@Builder
public record FinishedMatchesPage(
        int pageNumber,
        List<Match> matches,
        int totalPages,
        String filterByPlayerName,
        boolean hasFilter,
        boolean shouldShowPagination,
        boolean hasPrevious,
        boolean hasNext,
        int previousPage,
        int nextPage) {
}
