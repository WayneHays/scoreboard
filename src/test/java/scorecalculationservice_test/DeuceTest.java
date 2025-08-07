package scorecalculationservice_test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeuceTest extends ScoreCalculationTestBase {

    @Test
    void shouldNotHaveDeuceInitially() {
        assertFalse(score.isDeuce());
        assertNull(score.getAdvantage());
    }

    @Test
    void shouldSetDeuceFlagTrueAndAdvantageIsNull() {
        setPointsToDeuce();
        assertTrue(score.isDeuce());
        assertNull(score.getAdvantage());
    }

    @Test
    void shouldSetAdvantageIfPlayerWinsPoint() {
        setPointsToDeuce();
        service.calculate(matchWithScore, player1);

        assertTrue(score.isDeuce());
        assertEquals(player1, score.getAdvantage());
        assertEquals(40, score.getPoints(player1));
        assertEquals(40, score.getPoints(player2));
    }

    @Test
    void shouldIncrementGameAndResetPointsIfPlayerWinsPoint() {
        setPointsToDeuce();
        service.calculate(matchWithScore, player1);
        service.calculate(matchWithScore, player1);

        assertEquals(0, score.getPoints(player1));
        assertEquals(1, score.getGames(player1));
        assertEquals(0, score.getPoints(player2));
        assertEquals(0, score.getGames(player2));
        assertFalse(score.isDeuce());
        assertNull(score.getAdvantage());
    }

    @Test
    void shouldResetAdvantageAndRollbackPointsIfPlayerWinsPoint() {
        setPointsToDeuce();
        service.calculate(matchWithScore, player1);
        service.calculate(matchWithScore, player2);

        assertNull(score.getAdvantage());
        assertTrue(score.isDeuce());
    }

    @Test
    void shouldSideToSideAdvantage() {
        setPointsToDeuce();

        service.calculate(matchWithScore, player1);
        assertTrue(score.isDeuce());
        assertEquals(player1, score.getAdvantage());
        service.calculate(matchWithScore, player2);
        assertTrue(score.isDeuce());
        assertNull(score.getAdvantage());
        service.calculate(matchWithScore, player2);
        assertTrue(score.isDeuce());
        assertEquals(player2, score.getAdvantage());
    }

    @Test
    void shouldMaintainPointsAt40DuringDeuce() {
        setPointsToDeuce();
        service.calculate(matchWithScore, player2);
        assertEquals(40, score.getPoints(player1));
        assertEquals(40, score.getPoints(player2));
    }

    @Test
    void shouldTransitToTiebreakAfterDeuceGameAt5_6() {
        setGamesCount(5, 6);
        setPointsToDeuce();

        service.calculate(matchWithScore, player1);
        service.calculate(matchWithScore, player1);

        assertEquals(6, score.getGames(player1));
        assertEquals(6, score.getGames(player2));
        assertTrue(score.isTieBreak());
        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
        assertFalse(score.isDeuce());
        assertNull(score.getAdvantage());
    }

    private void setPointsToDeuce() {
        score.setPoints(player1, 40);
        score.setPoints(player2, 40);
        score.setDeuce(true);
    }

    private void setGamesCount(int player1games, int player2games) {
        score.setGames(player1, player1games);
        score.setGames(player2, player2games);
    }
}
