package com.scoreboard.util;

import java.util.regex.Pattern;

public final class PlayerNameValidator {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 30;
    private static final Pattern VALID_NAME = Pattern.compile("^[a-zA-Zа-яёА-ЯЁ\\s-']+$");

    private PlayerNameValidator() {
    }

    public static ValidationResult validate(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ValidationResult.error("Player name is required");
        }
        String trimmed = name.trim();

        if (trimmed.length() < MIN_LENGTH) {
            return ValidationResult.error("Name too short (minimum " + MIN_LENGTH + " characters)");
        }

        if (trimmed.length() > MAX_LENGTH) {
            return ValidationResult.error("Name too long (maximum " + MAX_LENGTH + " characters)");
        }

        if (!VALID_NAME.matcher(trimmed).matches()) {
            return ValidationResult.error("Name contains invalid characters");
        }

        return ValidationResult.success(trimmed);
    }
}
