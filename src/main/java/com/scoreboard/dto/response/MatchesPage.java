package com.scoreboard.dto.response;

import com.scoreboard.dto.FinishedMatchDto;
import lombok.Builder;

import java.util.List;

@Builder
public record MatchesPage(
        int pageNumber,
        List<FinishedMatchDto> matches,
        int totalPages,
        String playerName,
        List<String> errors) {
}
