package scorecalculationservice_test;

import com.scoreboard.model.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeuceTest extends ScoreCalculationTestBase {

    @Test
    void shouldNotHaveDeuceInitially() {
        System.out.println("=== shouldNotHaveDeuceInitially ===");
        logScore("Initial state");

        GameState gameState = service.getCurrentGameState(match, score);
        boolean isDeuce = service.isDeuce(score, player1, player2);

        System.out.println("isDeuce: " + isDeuce);
        System.out.println("advantagePlayer: " + gameState.advantagePlayer());

        assertFalse(isDeuce);
        assertNull(gameState.advantagePlayer());
        System.out.println("Test passed\n");
    }

    @Test
    void shouldSetDeuceFlagTrueAndAdvantageIsNull() {
        System.out.println("=== shouldSetDeuceFlagTrueAndAdvantageIsNull ===");
        setPointsToDeuce();
        logScore("After setting to deuce");

        GameState gameState = service.getCurrentGameState(match, score);
        boolean isDeuce = service.isDeuce(score, player1, player2);

        System.out.println("isDeuce: " + isDeuce);
        System.out.println("advantagePlayer: " + gameState.advantagePlayer());

        assertTrue(isDeuce);
        assertNull(gameState.advantagePlayer());
        System.out.println("Test passed\n");
    }

    @Test
    void shouldSetAdvantageIfPlayerWinsPoint() {
        System.out.println("=== shouldSetAdvantageIfPlayerWinsPoint ===");
        setPointsToDeuce();
        logScore("After setting to deuce");

        GameState gameState = service.calculate(match, score, player1);
        logScore("After player1 wins point");

        boolean isDeuce = service.isDeuce(score, player1, player2);
        System.out.println("isDeuce: " + isDeuce);
        System.out.println("advantagePlayer: " + gameState.advantagePlayer());

        // После выигрыша очка при деусе игрок получает преимущество
        assertEquals(player1, gameState.advantagePlayer());
        assertEquals(41, score.getPoints(player1)); // 40 + 1
        assertEquals(40, score.getPoints(player2));

        assertFalse(service.isDeuce(score, player1, player2));
        System.out.println("Test passed\n");
    }

    @Test
    void shouldIncrementGameAndResetPointsIfPlayerWinsPoint() {
        System.out.println("=== shouldIncrementGameAndResetPointsIfPlayerWinsPoint ===");
        setPointsToDeuce();
        logScore("After setting to deuce");

        service.calculate(match, score, player1); // Получаем преимущество (41:40)
        logScore("After player1 gets advantage");

        GameState gameState = service.calculate(match, score, player1); // Выигрываем гейм (42:40)
        logScore("After player1 wins game");

        boolean isDeuce = service.isDeuce(score, player1, player2);
        System.out.println("isDeuce: " + isDeuce);
        System.out.println("advantagePlayer: " + gameState.advantagePlayer());

        // После выигрыша гейма очки сбрасываются
        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
        assertEquals(1, score.getGames(player1));
        assertEquals(0, score.getGames(player2));
        assertFalse(service.isDeuce(score, player1, player2));
        assertNull(gameState.advantagePlayer());
        System.out.println("Test passed\n");
    }

    @Test
    void shouldResetAdvantageAndRollbackPointsIfPlayerWinsPoint() {
        System.out.println("=== shouldResetAdvantageAndRollbackPointsIfPlayerWinsPoint ===");
        setPointsToDeuce();
        logScore("After setting to deuce");

        service.calculate(match, score, player1); // player1 получает преимущество (41:40)
        logScore("After player1 gets advantage");

        GameState gameState = service.calculate(match, score, player2); // player2 сравнивает
        logScore("After player2 equalizes");

        boolean isDeuce = service.isDeuce(score, player1, player2);
        System.out.println("isDeuce: " + isDeuce);
        System.out.println("advantagePlayer: " + gameState.advantagePlayer());

        // Преимущество сбрасывается, возврат к деусу 40:40
        assertNull(gameState.advantagePlayer());
        assertTrue(service.isDeuce(score, player1, player2));
        assertEquals(40, score.getPoints(player1));
        assertEquals(40, score.getPoints(player2));
        System.out.println("Test passed\n");
    }

    @Test
    void shouldSideToSideAdvantage() {
        System.out.println("=== shouldSideToSideAdvantage ===");
        setPointsToDeuce();
        logScore("After setting to deuce");

        // player1 получает преимущество
        GameState gameState1 = service.calculate(match, score, player1);
        logScore("After player1 gets advantage");
        System.out.println("gameState1.advantagePlayer: " + gameState1.advantagePlayer());
        assertEquals(player1, gameState1.advantagePlayer());
        assertFalse(service.isDeuce(score, player1, player2)); // При преимуществе уже не деус

        // player2 сравнивает, возврат к деусу
        GameState gameState2 = service.calculate(match, score, player2);
        logScore("After player2 equalizes");
        System.out.println("gameState2.advantagePlayer: " + gameState2.advantagePlayer());
        assertNull(gameState2.advantagePlayer());
        assertTrue(service.isDeuce(score, player1, player2)); // Возврат к деусу 40:40

        // player2 получает преимущество
        GameState gameState3 = service.calculate(match, score, player2);
        logScore("After player2 gets advantage");
        System.out.println("gameState3.advantagePlayer: " + gameState3.advantagePlayer());
        assertEquals(player2, gameState3.advantagePlayer());
        assertFalse(service.isDeuce(score, player1, player2)); // При преимуществе уже не деус
        System.out.println("Test passed\n");
    }

    @Test
    void shouldMaintainPointsAt40DuringDeuce() {
        System.out.println("=== shouldMaintainPointsAt40DuringDeuce ===");
        setPointsToDeuce();
        logScore("After setting to deuce");

        GameState gameState = service.calculate(match, score, player2);
        logScore("After player2 wins point");

        boolean isDeuce = service.isDeuce(score, player1, player2);
        System.out.println("isDeuce: " + isDeuce);

        // После получения преимущества очки остаются >= 40
        assertTrue(score.getPoints(player1) >= 40);
        assertTrue(score.getPoints(player2) >= 40);
        // Но это уже не деус, а преимущество
        assertFalse(isDeuce);
        // player2 должен иметь преимущество
        assertEquals(player2, gameState.advantagePlayer());
        System.out.println("Test passed\n");
    }

    @Test
    void shouldTransitToTiebreakAfterDeuceGameAt6_6() {
        System.out.println("=== shouldTransitToTiebreakAfterDeuceGameAt6_6 ===");
        setGamesCount(5, 6);
        setPointsToDeuce();
        logScore("After setting games 5:6 and points to deuce");

        service.calculate(match, score, player1);
        logScore("After player1 gets advantage");

        GameState gameState = service.calculate(match, score, player1);
        logScore("After player1 wins game");

        boolean isDeuce = service.isDeuce(score, player1, player2);
        System.out.println("isDeuce: " + isDeuce);
        System.out.println("isTieBreak: " + gameState.isTieBreak());
        System.out.println("advantagePlayer: " + gameState.advantagePlayer());

        assertEquals(6, score.getGames(player1));
        assertEquals(6, score.getGames(player2));
        assertTrue(gameState.isTieBreak());
        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
        assertFalse(isDeuce);
        assertNull(gameState.advantagePlayer());
        System.out.println("Test passed\n");
    }

    private void setPointsToDeuce() {
        score.setPoints(player1, 40);
        score.setPoints(player2, 40);
    }

    private void setGamesCount(int player1games, int player2games) {
        score.setGames(player1, player1games);
        score.setGames(player2, player2games);
    }

    private void logScore(String moment) {
        System.out.println(moment + ":");
        System.out.println("  Points: " + score.getPoints(player1) + ":" + score.getPoints(player2));
        System.out.println("  Games: " + score.getGames(player1) + ":" + score.getGames(player2));
        System.out.println("  Sets: " + score.getSets(player1) + ":" + score.getSets(player2));
        System.out.println("  TieBreakPoints: " + score.getTieBreakPoints(player1) + ":" + score.getTieBreakPoints(player2));
    }
}
