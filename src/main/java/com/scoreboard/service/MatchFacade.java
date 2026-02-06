package com.scoreboard.service;

import com.scoreboard.dto.MatchResponse.FinishedMatchDto;
import com.scoreboard.dto.MatchResponse.MatchResponse;
import com.scoreboard.dto.MatchResultDto;
import com.scoreboard.dto.MatchResponse.OngoingMatchDto;
import com.scoreboard.mapper.FinishedMatchMapper;
import com.scoreboard.mapper.MatchResultMapper;
import com.scoreboard.mapper.OngoingMatchMapper;
import com.scoreboard.domain.model.OngoingMatch;
import com.scoreboard.domain.model.TennisPlayer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class MatchFacade {
    private static final String LOG_MATCH_FINISHED = "Match is finished, start saving to db";

    private final OngoingMatchService ongoingMatchService;
    private final FinishedMatchPersistenceService finishedMatchPersistenceService;
    private final OngoingMatchMapper ongoingMatchMapper;
    private final FinishedMatchMapper finishedMatchMapper;
    private final MatchResultMapper resultMapper;

    public MatchResponse processPoint(UUID matchId, String scorerName) {
        log.debug("Processing point for match: {}, player: {}", matchId, scorerName);

        TennisPlayer scorer = ongoingMatchService.ensurePlayerInMatch(matchId, scorerName);
        OngoingMatch updatedMatch = ongoingMatchService.computeMatch(matchId, match -> match.awardPoint(scorer));

        if (updatedMatch.isFinished()) {
            log.info(LOG_MATCH_FINISHED);

            FinishedMatchDto finishedMatch = finishedMatchMapper.toDto(updatedMatch);
            finishedMatchPersistenceService.saveFinishedMatch(finishedMatch);
            ongoingMatchService.removeMatch(matchId);
            MatchResultDto result = resultMapper.toDto(updatedMatch);
            return MatchResponse.finished(result);
        }

        OngoingMatchDto matchDto = ongoingMatchMapper.toDto(updatedMatch);
        return MatchResponse.ongoing(matchDto);
    }

    public OngoingMatchDto getOngoingMatch(UUID matchId) {
        OngoingMatch match = ongoingMatchService.getMatch(matchId);
        return ongoingMatchMapper.toDto(match);
    }
}
