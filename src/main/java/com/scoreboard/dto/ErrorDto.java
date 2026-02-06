package com.scoreboard.dto;

import lombok.Builder;

@Builder
public record ErrorDto(
        int statusCode,
        String icon,
        String title,
        String defaultMessage,
        String message,
        String requestedUrl
) {
}
