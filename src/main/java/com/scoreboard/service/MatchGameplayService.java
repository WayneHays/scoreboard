package com.scoreboard.service;

import com.scoreboard.dto.OngoingMatch;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.mapper.MatchResultMapper;

public class MatchGameplayService {
    private static final MatchGameplayService INSTANCE = new MatchGameplayService();

    private final ScoreCalculationService scoreCalculationService = ScoreCalculationService.getInstance();
    private final MatchResultMapper mapper = new MatchResultMapper();

    public static MatchGameplayService getInstance() {
        return INSTANCE;
    }

    public void processPoint(OngoingMatch ongoingMatch, String playerId) {
        Player pointWinner = findPlayerInMatch(ongoingMatch, playerId);
        scoreCalculationService.calculate(ongoingMatch, pointWinner);
    }


    public boolean isMatchFinished(OngoingMatch ongoingMatch) {
        return scoreCalculationService.isMatchFinished(
                ongoingMatch.getScore(),
                ongoingMatch.getMatch().getFirstPlayer(),
                ongoingMatch.getMatch().getSecondPlayer()
        );
    }

    public Player getMatchWinner(OngoingMatch ongoingMatch) {
        if (!isMatchFinished(ongoingMatch)) {
            throw new ValidationException("Match is not finished yet");
        }

        return scoreCalculationService.getMatchWinner(
                ongoingMatch.getMatch(),
                ongoingMatch.getScore()
        );
    }

    public Player findPlayerInMatch(OngoingMatch ongoingMatch, String playerId) {
        Match match = ongoingMatch.getMatch();

        if (playerId == null || playerId.isBlank()) {
            throw new ValidationException("Player ID is required");
        }

        if (playerId.equals(match.getFirstPlayer().getId().toString())) {
            return match.getFirstPlayer();
        }
        if (playerId.equals(match.getSecondPlayer().getId().toString())) {
            return match.getSecondPlayer();
        }
        throw new ValidationException("Player with ID " + playerId + " not found in this match");
    }

    public void setMatchWinner(OngoingMatch ongoingMatch) {
        Player winner = getMatchWinner(ongoingMatch);
        ongoingMatch.getMatch().setWinner(winner);
    }
}
