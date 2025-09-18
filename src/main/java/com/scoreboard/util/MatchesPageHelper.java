package com.scoreboard.util;

public final class MatchesPageHelper {

    public static boolean shouldShowPagination(int totalPages) {
        return totalPages > 1;
    }

    public static String buildPageUrl(int pageNum, String filter) {
        StringBuilder url = new StringBuilder("?page=" + pageNum);
        if (filter != null && !filter.trim().isEmpty()) {
            url.append("&filter_by_player_name=").append(filter);
        }
        return url.toString();
    }

    public static boolean hasFilter(String filterValue) {
        return filterValue != null && !filterValue.trim().isEmpty();
    }
}
