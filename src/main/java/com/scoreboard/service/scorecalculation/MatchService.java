package com.scoreboard.service.scorecalculation;

import com.scoreboard.dto.MatchDto;
import com.scoreboard.exception.MatchNotFoundException;
import com.scoreboard.mapper.OngoingMatchMapper;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.model.domain.TennisPlayer;
import com.scoreboard.service.FinishedMatchPersistenceService;
import com.scoreboard.service.OngoingMatchesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class MatchService {
    private static final String MATCH_NOT_FOUND_MSG = "Match not found";

    private final OngoingMatchesService ongoingMatchesService;
    private final FinishedMatchPersistenceService finishedMatchPersistenceService;
    private final OngoingMatchMapper mapper;

    public MatchDto awardPoint(UUID matchId, String scorerName) {
        TennisPlayer scorer = ongoingMatchesService.ensurePlayerInMatch(matchId, scorerName);

        log.debug("Processing point for match: {}, player: {}", matchId, scorerName);
        OngoingMatch updatedMatch = ongoingMatchesService.computeMatch(matchId, match -> match.awardPoint(scorer));

        if (updatedMatch.isFinished()) {
            finishedMatchPersistenceService.saveFinishedMatch(updatedMatch);
            ongoingMatchesService.removeMatch(matchId);
        }
        return mapper.toDto(updatedMatch);
    }

    public MatchDto getMatchDto(UUID matchId) {
        OngoingMatch match = ongoingMatchesService.getMatch(matchId)
                .orElseThrow(() -> new MatchNotFoundException(MATCH_NOT_FOUND_MSG));
        return mapper.toDto(match);
    }
}
