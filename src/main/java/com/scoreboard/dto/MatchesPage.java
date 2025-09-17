package com.scoreboard.dto;

import com.scoreboard.model.Match;

import java.util.List;

public record MatchesPage(int pageNumber,
                          List<Match> matches,
                          int totalPages,
                          String filterByPlayerName,
                          String errorMessage) {

    public MatchesPage(int pageNumber, List<Match> matches, int totalPages) {
        this(pageNumber, matches, totalPages, null, null);
    }

    public MatchesPage(int pageNumber, List<Match> matches, int totalPages, String filterByPlayerName) {
        this(pageNumber, matches, totalPages, filterByPlayerName, null);
    }

    public boolean hasFilter() {
        return filterByPlayerName != null && !filterByPlayerName.trim().isEmpty();
    }

    public boolean hasError() {
        return errorMessage != null && !errorMessage.trim().isEmpty();
    }
}
