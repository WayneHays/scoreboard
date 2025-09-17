package scorecalculationservice_test;

import com.scoreboard.model.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class GameplayTest extends ScoreCalculationTestBase {

    @Test
    void shouldReturnIfMatchFinished() {
        score.setSets(player1, 1);
        score.setSets(player2, 1);
        score.setGames(player1, 5);
        score.setGames(player2, 0);
        score.setPoints(player1, 40);
        score.setPoints(player2, 0);

        GameState gameState = service.calculate(match, score, player1);

        assertSame(score, gameState.score());
        assertTrue(service.isMatchFinished(score, player1, player2));
    }

    @Test
    void shouldIncrementCountOfPoints() {
        service.calculate(match, score, player1);
        assertEquals(15, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));

        service.calculate(match, score, player1);
        assertEquals(30, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));

        service.calculate(match, score, player1);
        assertEquals(40, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));

        service.calculate(match, score,player1);
        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
    }

    @Test
    void shouldIncrementCountOfGames() {
        service.calculate(match, score, player1);
        service.calculate(match, score, player1);
        service.calculate(match, score, player1);
        service.calculate(match, score, player1);

        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
        assertEquals(1, score.getGames(player1));
        assertEquals(0, score.getGames(player2));
    }
}
