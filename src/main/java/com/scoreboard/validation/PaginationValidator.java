package com.scoreboard.validation;

import com.scoreboard.exception.NotFoundException;
import com.scoreboard.exception.ValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationValidator {

    public static void validatePageSize(int pageSize) {
        if (pageSize <= 0) {
            throw new ValidationException(
                    "Page size must be positive, got: %d".formatted(pageSize));
        }
    }

    public static void validatePageNumber(int pageNumber) {
        if (pageNumber < 1) {
            throw new ValidationException("Page number must be positive, got: " + pageNumber);
        }
    }

    public static void validatePageNumberAgainstTotal(int pageNumber, long totalPages) {
        if (totalPages == 0) {
            if (pageNumber > 1) {
                throw new NotFoundException("No matches found");
            }
        } else if (pageNumber > totalPages) {
            throw new NotFoundException(
                    String.format("Page %d not found. Available pages: 1-%d", pageNumber, totalPages)
            );
        }
    }
}
