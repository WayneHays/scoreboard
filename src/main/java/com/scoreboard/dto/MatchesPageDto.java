package com.scoreboard.dto;

import com.scoreboard.dto.MatchResponse.FinishedMatchDto;
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
