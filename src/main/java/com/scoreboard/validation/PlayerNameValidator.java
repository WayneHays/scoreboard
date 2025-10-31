package com.scoreboard.validation;

import com.scoreboard.exception.ValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerNameValidator {
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 30;
    private static final Pattern VALID_NAME_PATTERN =
            Pattern.compile("^(?!.*[\\-\\s']{2})[A-Za-zА-Яа-яЁё]+(?:[\\s\\-'][A-Za-zА-Яа-яЁё]+)*$");

    private static final Set<String> RESERVED_NAMES = Set.of(
            "null", "undefined", "none", "unknown", "anonymous",
            "system", "admin", "administrator", "root", "guest",
            "player", "user", "test", "demo"
    );

    private static final String ERROR_EMPTY = "Player name is required";
    private static final String ERROR_NULL = "Player name cannot be null";
    private static final String ERROR_TOO_SHORT = "Name must be at least " + MIN_NAME_LENGTH + " characters";
    private static final String ERROR_TOO_LONG = "Name cannot exceed " + MAX_NAME_LENGTH + " characters";
    private static final String ERROR_INVALID_CHARS =
            "Name can contain letters, single spaces, hyphens or apostrophes. " +
            "Cannot start/end with special characters or have them consecutively.";
    private static final String ERROR_RESERVED_WORDS = "Name cannot be a reserved word";

    public static String validate(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException(ERROR_EMPTY);
        }

        String trimmed = name.trim();

        if (trimmed.length() < MIN_NAME_LENGTH) {
            throw new ValidationException(ERROR_TOO_SHORT);
        }

        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new ValidationException(ERROR_TOO_LONG);
        }

        if (!VALID_NAME_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException(ERROR_INVALID_CHARS);
        }

        if (RESERVED_NAMES.contains(trimmed.toLowerCase())) {
            throw new ValidationException(ERROR_RESERVED_WORDS);
        }

        if (trimmed.equals("null")) {
            throw new ValidationException(ERROR_NULL);
        }

        return trimmed;
    }
}
