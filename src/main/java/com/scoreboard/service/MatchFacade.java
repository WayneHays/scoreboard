package com.scoreboard.service;

import com.scoreboard.dto.FinishedMatchDto;
import com.scoreboard.dto.OngoingMatchDto;
import com.scoreboard.mapper.FinishedMatchMapper;
import com.scoreboard.mapper.OngoingMatchMapper;
import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.TennisPlayer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class MatchFacade {
    private final OngoingMatchService ongoingMatchService;
    private final FinishedMatchPersistenceService finishedMatchPersistenceService;
    private final OngoingMatchMapper ongoingMatchMapper;
    private final FinishedMatchMapper finishedMatchMapper;

    public OngoingMatchDto awardPoint(UUID matchId, String scorerName) {
        TennisPlayer scorer = ongoingMatchService.ensurePlayerInMatch(matchId, scorerName);

        log.debug("Processing point for match: {}, player: {}", matchId, scorerName);
        OngoingMatch updatedMatch = ongoingMatchService.computeMatch(matchId, match -> match.awardPoint(scorer));

        if (updatedMatch.isFinished()) {
            FinishedMatchDto dto = finishedMatchMapper.toDto(updatedMatch);
            finishedMatchPersistenceService.saveFinishedMatch(dto);
            ongoingMatchService.removeMatch(matchId);
        }
        return ongoingMatchMapper.toDto(updatedMatch);
    }

    public OngoingMatchDto getMatchDto(UUID matchId) {
        OngoingMatch match = ongoingMatchService.getMatch(matchId);
        return ongoingMatchMapper.toDto(match);
    }
}
