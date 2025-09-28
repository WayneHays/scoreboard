package com.scoreboard.dto;

import lombok.Builder;

@Builder
public record ErrorPageData(
        int statusCode,
        String errorIcon,
        String errorTitle,
        String defaultMessage,
        String errorMessage,
        String requestedUrl
) {
}
