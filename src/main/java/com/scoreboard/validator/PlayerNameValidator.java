package com.scoreboard.validator;

import com.scoreboard.exception.ValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerNameValidator {
    private static final int MAX_PLAYER_NAME_LENGTH = 30;
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яёА-ЯЁ -']+$");
    private static final String MESSAGE_FOR_USER =
            "Name can only contain letters, spaces, hyphens and apostrophes (e.g., John Doe, Mary-Jane, O'Brien)";

    public static String validate(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Player name is required");
        }
        String trimmedName = name.trim();

        if (trimmedName.length() > MAX_PLAYER_NAME_LENGTH) {
            throw new ValidationException("Name too long (maximum %d characters)".formatted(MAX_PLAYER_NAME_LENGTH));
        }

        if (!VALID_NAME_PATTERN.matcher(trimmedName).matches()) {
            throw new ValidationException("Name contains invalid characters (%s) symbols only)"
                    .formatted(MESSAGE_FOR_USER));
        }

        return trimmedName;
    }
}
