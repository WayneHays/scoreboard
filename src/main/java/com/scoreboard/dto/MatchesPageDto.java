package com.scoreboard.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record MatchesPageDto(
        int pageNumber,
        List<FinishedMatchDto> matches,
        int totalPages,
        String playerName,
        List<String> errors) {
}
