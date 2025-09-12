package com.scoreboard.util;

import java.util.regex.Pattern;

public final class PlayerNameValidator {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 30;
    private static final Pattern VALID_NAME = Pattern.compile("^[a-zA-Zа-яёА-ЯЁ\\s-']+$");

    private static final String NAME_TOO_SHORT_TEMPLATE = "Name too short (minimum %d characters";
    private static final String PLAYER_NAME_REQUIRED_MSG = "Player name is required";
    private static final String INVALID_NAME_MSG = "Name contains invalid characters";
    private static final String NAME_TOO_LONG_TEMPLATE = "Name too long (maximum %d characters";

    private PlayerNameValidator() {
    }

    public static ValidationResult validate(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ValidationResult.error(PLAYER_NAME_REQUIRED_MSG);
        }
        String trimmed = name.trim();

        if (trimmed.length() < MIN_LENGTH) {
            return ValidationResult.error(NAME_TOO_SHORT_TEMPLATE.formatted(MIN_LENGTH));
        }

        if (trimmed.length() > MAX_LENGTH) {
            return ValidationResult.error(NAME_TOO_LONG_TEMPLATE.formatted(MAX_LENGTH));
        }

        if (!VALID_NAME.matcher(trimmed).matches()) {
            return ValidationResult.error(INVALID_NAME_MSG);
        }

        return ValidationResult.success(trimmed);
    }
}
