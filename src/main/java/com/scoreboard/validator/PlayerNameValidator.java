package com.scoreboard.validator;

import java.util.regex.Pattern;

public class PlayerNameValidator {
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 30;
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яёА-ЯЁ\\s-']+$");

    public ValidationResult validate(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ValidationResult.error("Player name is required");
        }
        String trimmed = name.trim();

        if (trimmed.length() < MIN_NAME_LENGTH) {
            return ValidationResult.error("Name too short (minimum %d characters)".formatted(MIN_NAME_LENGTH));
        }

        if (trimmed.length() > MAX_NAME_LENGTH) {
            return ValidationResult.error("Name too long (maximum %d characters)".formatted(MAX_NAME_LENGTH));
        }

        if (!VALID_NAME_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult.error("Name contains invalid characters (a-zA-Zа-яёА-ЯЁ symbols only)");
        }

        return ValidationResult.success(trimmed);
    }
}
