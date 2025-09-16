package scorecalculationservice_test;

import com.scoreboard.dto.GameState;
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

        GameState result = service.calculate(matchWithScore, player1);

        assertSame(score, result.score());
        assertTrue(service.isMatchFinished(result.score(), player1, player2));
    }

    @Test
    void shouldIncrementCountOfPoints() {
        service.calculate(matchWithScore, player1);
        assertEquals(15, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));

        service.calculate(matchWithScore, player1);
        assertEquals(30, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));

        service.calculate(matchWithScore, player1);
        assertEquals(40, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));

        service.calculate(matchWithScore, player1);
        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
    }

    @Test
    void shouldIncrementCountOfGames() {
        service.calculate(matchWithScore, player1);
        service.calculate(matchWithScore, player1);
        service.calculate(matchWithScore, player1);
        service.calculate(matchWithScore, player1);

        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
        assertEquals(1, score.getGames(player1));
        assertEquals(0, score.getGames(player2));
    }
}
