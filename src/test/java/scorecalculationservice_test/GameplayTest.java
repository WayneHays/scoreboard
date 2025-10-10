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

        scoreCalculationService.winPoint(ongoingMatch, player1);
        scoreCalculationService.winPoint(ongoingMatch, player1);
        scoreCalculationService.winPoint(ongoingMatch, player1);
        scoreCalculationService.winPoint(ongoingMatch, player1);

        assertEquals(player1, ongoingMatch.getWinner());
        assertEquals(2, ongoingMatch.getSets(player1));
        assertEquals(1, ongoingMatch.getSets(player2));
    }

    @Test
    void shouldIncrementPointsCorrectly() {
        scoreCalculationService.winPoint(ongoingMatch, player1);
        assertEquals(15, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));

        scoreCalculationService.winPoint(ongoingMatch, player1);
        assertEquals(30, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));

        scoreCalculationService.winPoint(ongoingMatch, player1);
        assertEquals(40, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));

        scoreCalculationService.winPoint(ongoingMatch, player1);
        assertEquals(0, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));
        assertEquals(1, ongoingMatch.getGames(player1));
    }

    @Test
    void shouldIncrementGameCountAfterWinning4Points() {
        scoreCalculationService.winPoint(ongoingMatch, player1);
        scoreCalculationService.winPoint(ongoingMatch, player1);
        scoreCalculationService.winPoint(ongoingMatch, player1);
        scoreCalculationService.winPoint(ongoingMatch, player1);

        assertEquals(0, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));
        assertEquals(1, ongoingMatch.getGames(player1));
        assertEquals(0, ongoingMatch.getGames(player2));
    }

    @Test
    void shouldNotFinishMatchIfNotEnoughSets() {
        ongoingMatch.awardSet(player1);

        assertNull(ongoingMatch.getWinner());
        assertEquals(1, ongoingMatch.getSets(player1));
        assertEquals(0, ongoingMatch.getSets(player2));
    }
}
