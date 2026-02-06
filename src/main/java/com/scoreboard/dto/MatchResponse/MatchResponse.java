package com.scoreboard.dto.MatchResponse;

import com.scoreboard.dto.MatchResultDto;

public record MatchResponse(
        OngoingMatchDto ongoingMatch,
        MatchResultDto matchResult
) {

    public static MatchResponse ongoing(OngoingMatchDto dto) {
        return new MatchResponse(dto, null);
    }

    public static MatchResponse finished(MatchResultDto dto) {
        return new MatchResponse(null, dto);
    }

    public boolean isFinished() {
        return matchResult != null;
    }
}
