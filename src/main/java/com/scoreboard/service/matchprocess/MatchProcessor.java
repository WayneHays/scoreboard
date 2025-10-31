package com.scoreboard.service.matchprocess;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.service.finishedmatchpersistence.FinishedMatchPersistenceService;
import com.scoreboard.service.ongoingmatches.OngoingMatchesService;
import com.scoreboard.service.scorecalculation.ScoreCalculationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@AllArgsConstructor
public class MatchProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MatchProcessor.class);

    private final OngoingMatchesService ongoingMatchesService;
    private final ScoreCalculationService scoreCalculationService;
    private final FinishedMatchPersistenceService finishedMatchPersistenceService;
    private final MatchLockManager lockManager;

    public OngoingMatch getMatch(UUID matchId) {
        return ongoingMatchesService.get(matchId);
    }

    public void processPoint(UUID matchId, String playerName) {
        lockManager.executeWithLock(matchId, () -> {
            if (!ongoingMatchesService.contains(matchId)) {
                return;
            }

            OngoingMatch match = ongoingMatchesService.get(matchId);

            if (match.isFinished()) {
                return;
            }

            Player scorer = match.getPlayerByName(playerName);
            scoreCalculationService.awardPoint(match, scorer);
            logger.debug("Point awarded to player: {}", playerName);

            if (match.isFinished()) {
                logger.info("Match {} completed, winner: {}", matchId, match.getWinner().getName());
                finishedMatchPersistenceService.saveFinishedMatch(match);
                scheduleDeletion(matchId);
                lockManager.releaseLock(matchId);
            }
        });
    }

    private void scheduleDeletion(UUID matchId) {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                ongoingMatchesService.delete(matchId);
                logger.debug("Match {} removed from ongoing matches", matchId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                ongoingMatchesService.delete(matchId);
            }
        }).start();
    }
}
