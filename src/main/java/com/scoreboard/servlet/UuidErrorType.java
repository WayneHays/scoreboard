package com.scoreboard.servlet;

import lombok.Getter;

@Getter
public enum UuidErrorType {
    MATCH_ID_REQUIRED("Match ID is required", "Please provide a valid match ID in the URL."),
    INVALID_FORMAT("Invalid match ID format", "The provided match ID is not in the correct format."),
    MATCH_NOT_FOUND("Match not found", "No active match found with this ID.");

    private final String title;
    private final String message;

    UuidErrorType(String title, String message) {
        this.title = title;
        this.message = message;
    }
}
