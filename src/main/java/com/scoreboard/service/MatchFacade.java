package com.scoreboard.service;

import com.scoreboard.dto.FinishedMatchDto;
import com.scoreboard.dto.MatchResultDto;
import com.scoreboard.dto.OngoingMatchDto;
import com.scoreboard.mapper.FinishedMatchMapper;
import com.scoreboard.mapper.MatchResultMapper;
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
    private final MatchResultMapper resultMapper;

    public OngoingMatchDto awardPoint(UUID matchId, String scorerName) {
        TennisPlayer scorer = ongoingMatchService.ensurePlayerInMatch(matchId, scorerName);

        log.debug("Processing point for match: {}, player: {}", matchId, scorerName);
        OngoingMatch updatedMatch = ongoingMatchService.computeMatch(matchId, match -> match.awardPoint(scorer));

        if (updatedMatch.isFinished()) {
            FinishedMatchDto dto = finishedMatchMapper.toDto(updatedMatch);
            finishedMatchPersistenceService.saveFinishedMatch(dto);
        }
        return ongoingMatchMapper.toDto(updatedMatch);
    }

    public OngoingMatchDto getOngoingMatch(UUID matchId) {
        OngoingMatch match = ongoingMatchService.getMatch(matchId);
        return ongoingMatchMapper.toDto(match);
    }

    public MatchResultDto getMatchResultAndRemove(UUID matchId) {
        OngoingMatch match = ongoingMatchService.getMatch(matchId);
        MatchResultDto result = resultMapper.toDto(match);

        FinishedMatchDto finishedDto = finishedMatchMapper.toDto(match);
        finishedMatchPersistenceService.saveFinishedMatch(finishedDto);

        ongoingMatchService.removeMatch(matchId);

        return result;
    }
}
