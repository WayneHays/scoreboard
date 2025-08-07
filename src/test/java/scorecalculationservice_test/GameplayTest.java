package scorecalculationservice_test;

import com.scoreboard.model.Score;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class GameplayTest extends ScoreCalculationTestBase {

    @Test
    void shouldReturnIfMatchFinished() {
        score.setMatchFinished(true);

        int initialPlayer1Points = score.getPoints(player1);
        int initialPlayer1Games = score.getGames(player1);
        int initialPlayer1Sets = score.getSets(player1);
        int initialPlayer2Points = score.getPoints(player2);
        int initialPlayer2Games = score.getGames(player2);
        int initialPlayer2Sets = score.getSets(player2);

        Score result = service.calculate(matchWithScore, player1);

        assertEquals(initialPlayer1Points, score.getPoints(player1));
        assertEquals(initialPlayer1Games, score.getGames(player1));
        assertEquals(initialPlayer1Sets, score.getSets(player1));
        assertEquals(initialPlayer2Points, score.getPoints(player2));
        assertEquals(initialPlayer2Games, score.getGames(player2));
        assertEquals(initialPlayer2Sets, score.getSets(player2));

        assertSame(score, result);
        assertTrue(score.isMatchFinished());
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
