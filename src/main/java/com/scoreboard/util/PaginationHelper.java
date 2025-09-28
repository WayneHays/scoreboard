package com.scoreboard.util;

import com.scoreboard.config.ApplicationConfig;
import com.scoreboard.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationHelper {
    private static final int MINIMAL_ALLOWED_PAGE = 1;
    private static final String PAGE_NOT_FOUND_TEMPLATE = "Page %d not found. Available pages: 1-%d";

    public static int calculateTotalPages(int totalRecords) {
        return totalRecords == 0 ? 0 : (totalRecords - 1) / ApplicationConfig.PAGE_SIZE + 1;
    }

    public static void validatePageNumber(int pageNumber, int totalPages) {
        if (isInvalidPageNumber(pageNumber, totalPages)) {
            String message = (totalPages == 0) ? "No matches found" :
                    String.format(PAGE_NOT_FOUND_TEMPLATE, pageNumber, totalPages);
            throw new NotFoundException(message);
        }
    }

    public static <T> List<T> paginateInMemory(List<T> allItems, int pageNumber) {
        int startIndex = (pageNumber - 1) * ApplicationConfig.PAGE_SIZE;
        int endIndex = Math.min(startIndex + ApplicationConfig.PAGE_SIZE, allItems.size());
        return startIndex < allItems.size() ?
                allItems.subList(startIndex, endIndex) :
                new ArrayList<>();
    }

    private static boolean isInvalidPageNumber(int pageNumber, int totalPages) {
        return pageNumber < MINIMAL_ALLOWED_PAGE || (totalPages > 0 && pageNumber > totalPages);
    }
}
