package com.scoreboard.validator;

import com.scoreboard.exception.ValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerNameValidator {
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 30;
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яёА-ЯЁ\\s-']+$");

    public static String validate(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Player name is required");
        }
        String trimmedName = name.trim();

        if (trimmedName.length() < MIN_NAME_LENGTH) {
            throw new ValidationException("Name too short (minimum %d characters)".formatted(MIN_NAME_LENGTH));
        }

        if (trimmedName.length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Name too long (maximum %d characters)".formatted(MAX_NAME_LENGTH));
        }

        if (!VALID_NAME_PATTERN.matcher(trimmedName).matches()) {
            throw new ValidationException("Name contains invalid characters (a-zA-Zа-яёА-ЯЁ symbols only)");
        }

        return trimmedName;
    }
}
