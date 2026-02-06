package com.scoreboard.validation;

import com.scoreboard.validation.result.PairValidationResult;
import com.scoreboard.validation.result.SingleValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class PlayerNameValidator {
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 30;
    private static final Pattern VALID_NAME_PATTERN =
            Pattern.compile("^(?!.*[\\-\\s']{2})[A-Za-zА-Яа-яЁё]+(?:[\\s\\-'][A-Za-zА-Яа-яЁё]+)*$");

    private static final Set<String> RESERVED_NAMES = Set.of(
            "null", "undefined", "none", "unknown", "anonymous",
            "system", "admin", "administrator", "root", "guest",
            "player", "user", "test", "demo"
    );

    private static final String MSG_TOO_SHORT = "Name must be at least " + MIN_NAME_LENGTH + " characters";
    private static final String MSG_TOO_LONG = "Name cannot exceed " + MAX_NAME_LENGTH + " characters";
    private static final String MSG_INVALID_CHARS = """
            Name can contain letters, single spaces, hyphens or apostrophes.
            Cannot start/end with special characters or have them consecutively
            """;
    private static final String MSG_RESERVED_WORDS = "Name cannot be a reserved word";
    private static final String MSG_SAME_NAME = "Names must be different";

    public PairValidationResult validatePair(String firstName, String secondName) {
        SingleValidationResult firstNameResult = validate(firstName, MIN_NAME_LENGTH);
        SingleValidationResult secondNameResult = validate(secondName, MIN_NAME_LENGTH);
        List<String> commonErrors = validateDuplicateNames(firstName, secondName);

        return new PairValidationResult(commonErrors, firstNameResult.errors(), secondNameResult.errors());
    }

    public SingleValidationResult validate(String name, int minLength) {
        List<String> errors = new ArrayList<>();

        if (name.length() < minLength) {
            errors.add(MSG_TOO_SHORT);
        }

        if (name.length() > MAX_NAME_LENGTH) {
            errors.add(MSG_TOO_LONG);
        }

        if (!VALID_NAME_PATTERN.matcher(name).matches()) {
            errors.add(MSG_INVALID_CHARS);
        }

        if (RESERVED_NAMES.contains(name.toLowerCase())) {
            errors.add(MSG_RESERVED_WORDS);
        }

        return new SingleValidationResult(errors);
    }

    private List<String> validateDuplicateNames(String firstName, String secondName) {
        List<String> errors = new ArrayList<>();

        if (firstName.equalsIgnoreCase(secondName)) {
            errors.add(MSG_SAME_NAME);
        }
        return errors;
    }
}
