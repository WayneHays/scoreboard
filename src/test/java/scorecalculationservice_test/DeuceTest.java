package scorecalculationservice_test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeuceTest extends ScoreCalculationTestBase {

    @Test
    void shouldNotHaveAdvantageInitially() {
        assertNull(ongoingMatch.getAdvantageStatus());
    }

    @Test
    void shouldNotHaveAdvantageAtDeuce() {
        setPointsToDeuce();

        assertNull(ongoingMatch.getAdvantageStatus());
        assertEquals(40, ongoingMatch.getPoints(player1));
        assertEquals(40, ongoingMatch.getPoints(player2));
    }

    @Test
    void shouldSetAdvantageIfPlayerWinsPointAfterDeuce() {
        setPointsToDeuce();

        scoreCalculationService.winPoint(ongoingMatch, player1);

        assertEquals(player1, ongoingMatch.getAdvantageStatus());
        assertEquals(40, ongoingMatch.getPoints(player1));
        assertEquals(40, ongoingMatch.getPoints(player2));
    }

    @Test
    void shouldWinGameIfPlayerHasAdvantageAndWinsAnotherPoint() {
        setPointsToDeuce();

        scoreCalculationService.winPoint(ongoingMatch, player1);
        scoreCalculationService.winPoint(ongoingMatch, player1);

        assertEquals(0, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));
        assertEquals(1, ongoingMatch.getGames(player1));
        assertEquals(0, ongoingMatch.getGames(player2));
        assertNull(ongoingMatch.getAdvantageStatus());
    }

    @Test
    void shouldReturnToDeuceIfOpponentWinsPointWhenPlayerHasAdvantage() {
        setPointsToDeuce();

        scoreCalculationService.winPoint(ongoingMatch, player1);
        scoreCalculationService.winPoint(ongoingMatch, player2);

        assertNull(ongoingMatch.getAdvantageStatus());
        assertEquals(40, ongoingMatch.getPoints(player1));
        assertEquals(40, ongoingMatch.getPoints(player2));
    }

    @Test
    void shouldSwitchAdvantageBackAndForth() {
        setPointsToDeuce();

        scoreCalculationService.winPoint(ongoingMatch, player1);
        assertEquals(player1, ongoingMatch.getAdvantageStatus());

        scoreCalculationService.winPoint(ongoingMatch, player2);
        assertNull(ongoingMatch.getAdvantageStatus());

        scoreCalculationService.winPoint(ongoingMatch, player2);
        assertEquals(player2, ongoingMatch.getAdvantageStatus());
    }

    @Test
    void shouldTransitToTiebreakAfterDeuceGameAt6_6() {
        setGamesCount(5, 6);
        setPointsToDeuce();

        scoreCalculationService.winPoint(ongoingMatch, player1);
        scoreCalculationService.winPoint(ongoingMatch, player1);

        assertEquals(6, ongoingMatch.getGames(player1));
        assertEquals(6, ongoingMatch.getGames(player2));
        assertTrue(ongoingMatch.isTieBreak());
        assertEquals(0, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));
        assertNull(ongoingMatch.getAdvantageStatus());
    }

    private void setPointsToDeuce() {
        for (int i = 0; i < 3; i++) {
            scoreCalculationService.winPoint(ongoingMatch, player1);
            scoreCalculationService.winPoint(ongoingMatch, player2);
        }
    }

    private void setGamesCount(int player1games, int player2games) {
        ongoingMatch.resetAllGames();

        for (int i = 0; i < player1games; i++) {
            ongoingMatch.awardGame(player1);
        }
        for (int i = 0; i < player2games; i++) {
            ongoingMatch.awardGame(player2);
        }
    }
}
