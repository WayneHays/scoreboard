package com.scoreboard.validator;

public record ValidationResult(boolean isValid, String value, String errorMessage) {

    public static ValidationResult success(String value) {
        return new ValidationResult(true, value, null);
    }

    public static ValidationResult error(String message) {
        return new ValidationResult(false, null, message);
    }
}
