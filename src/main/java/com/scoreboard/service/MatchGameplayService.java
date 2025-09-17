package com.scoreboard.service;

import com.scoreboard.dto.OngoingMatch;
import com.scoreboard.model.GameState;
import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;
import com.scoreboard.util.MatchUtils;

public class MatchGameplayService {
    private static final MatchGameplayService INSTANCE = new MatchGameplayService();

    private final ScoreCalculationService scoreCalculationService = ScoreCalculationService.getInstance();

    public static MatchGameplayService getInstance() {
        return INSTANCE;
    }

    public void processPoint(OngoingMatch ongoingMatch, String playerId) {
        if (ongoingMatch == null) {
            throw new IllegalArgumentException("Match not found");
        }

        Player pointWinner = MatchUtils.findPlayerInMatch(ongoingMatch.getMatch(), playerId);
        Match match = ongoingMatch.getMatch();
        Score score = ongoingMatch.getGameState().score();

        GameState newGameState = scoreCalculationService.calculate(match, score, pointWinner);

        ongoingMatch.setGameState(newGameState);
    }

    public boolean isMatchFinished(OngoingMatch ongoingMatch) {
        if (ongoingMatch == null) {
            throw new IllegalArgumentException("Match not found");
        }

        Match match = ongoingMatch.getMatch();
        Score score = ongoingMatch.getGameState().score();

        return scoreCalculationService.isMatchFinished(
                score,
                match.getFirstPlayer(),
                match.getSecondPlayer()
        );
    }

    public Player getMatchWinner(OngoingMatch ongoingMatch) {
        if (ongoingMatch == null) {
            throw new IllegalArgumentException("Match not found");
        }

        if (!isMatchFinished(ongoingMatch)) {
            throw new IllegalStateException("Match is not finished yet");
        }

        Match match = ongoingMatch.getMatch();
        Score score = ongoingMatch.getGameState().score();

        int firstPlayerSets = score.getSets(match.getFirstPlayer());
        int secondPlayerSets = score.getSets(match.getSecondPlayer());

        return firstPlayerSets > secondPlayerSets
                ? match.getFirstPlayer()
                : match.getSecondPlayer();
    }
}
