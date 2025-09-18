package com.scoreboard.dto;

public record ErrorPageData(
        int statusCode,
        String errorIcon,
        String errorTitle,
        String defaultMessage,
        String errorMessage,
        String requestedUrl
) {
}
