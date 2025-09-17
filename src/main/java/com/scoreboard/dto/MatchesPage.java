package com.scoreboard.dto;

import com.scoreboard.model.Match;

import java.util.List;

public record MatchesPage(int pageNumber,
                          List<Match> matches,
                          int totalPages,
                          String filterByPlayerName,
                          String errorMessage) {

    private static final int PAGINATION_RANGE = 2;
    private static final int FIRST_PAGE_THRESHOLD = 3;
    private static final int FIRST_PAGE_DOTS_THRESHOLD = 4;
    private static final int LAST_PAGE_THRESHOLD = 2;
    private static final int LAST_PAGE_DOTS_THRESHOLD = 3;

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

    public String getPreviousPageUrl() {
        if (pageNumber <= 1) return null;
        return buildUrl(pageNumber - 1);
    }

    public String getNextPageUrl() {
        if (pageNumber >= totalPages) return null;
        return buildUrl(pageNumber + 1);
    }

    public String getPageUrl(int page) {
        return buildUrl(page);
    }

    private String buildUrl(int page) {
        StringBuilder url = new StringBuilder("?page=" + page);
        if (hasFilter()) {
            url.append("&filter_by_player_name=").append(filterByPlayerName);
        }
        return url.toString();
    }

    public boolean shouldShowFirstPage() {
        return pageNumber > FIRST_PAGE_THRESHOLD;
    }

    public boolean shouldShowFirstPageDots() {
        return pageNumber > FIRST_PAGE_DOTS_THRESHOLD;
    }

    public boolean shouldShowLastPage() {
        return pageNumber < totalPages - LAST_PAGE_THRESHOLD;
    }

    public boolean shouldShowLastPageDots() {
        return pageNumber < totalPages - LAST_PAGE_DOTS_THRESHOLD;
    }

    public int getStartPage() {
        return Math.max(1, pageNumber - PAGINATION_RANGE);
    }

    public int getEndPage() {
        return Math.min(totalPages, pageNumber + PAGINATION_RANGE);
    }

    public boolean shouldShowPagination() {
        return totalPages > 1;
    }

    public boolean shouldShowPageInfo() {
        return totalPages > 0;
    }

    public String getNoResultsMessage() {
        if (hasError()) {
            return "";
        }
        return hasFilter()
                ? "No matches found for player \"" + filterByPlayerName + "\""
                : "No matches found";
    }
}
