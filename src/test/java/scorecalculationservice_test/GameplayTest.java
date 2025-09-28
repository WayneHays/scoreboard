package scorecalculationservice_test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class GameplayTest extends ScoreCalculationTestBase {

    @Test
    void shouldFinishMatchWhenPlayerWins2Sets() {
        ongoingMatch.awardSet(player1);
        ongoingMatch.awardSet(player2);

        for (int i = 0; i < 5; i++) {
            ongoingMatch.awardGame(player1);
        }

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());

        assertTrue(service.isMatchFinished(ongoingMatch));
        assertEquals(2, ongoingMatch.getSets(player1));
        assertEquals(1, ongoingMatch.getSets(player2));
    }

    @Test
    void shouldIncrementPointsCorrectly() {
        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        assertEquals(15, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        assertEquals(30, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        assertEquals(40, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        assertEquals(0, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));
        assertEquals(1, ongoingMatch.getGames(player1));
    }

    @Test
    void shouldIncrementGameCountAfterWinning4Points() {
        // Выигрываем 4 очка подряд для победы в игре
        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());

        assertEquals(0, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));
        assertEquals(1, ongoingMatch.getGames(player1));
        assertEquals(0, ongoingMatch.getGames(player2));
    }

    @Test
    void shouldNotFinishMatchIfNotEnoughSets() {
        ongoingMatch.awardSet(player1);

        assertFalse(service.isMatchFinished(ongoingMatch));
        assertEquals(1, ongoingMatch.getSets(player1));
        assertEquals(0, ongoingMatch.getSets(player2));
    }
}
