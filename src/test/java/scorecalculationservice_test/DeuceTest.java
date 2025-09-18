package scorecalculationservice_test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeuceTest extends ScoreCalculationTestBase {

    @Test
    void shouldNotHaveDeuceInitially() {
        System.out.println("=== shouldNotHaveDeuceInitially ===");
        logScore("Initial state");

        boolean isDeuce = service.isDeuce(score, player1, player2);

        System.out.println("isDeuce: " + isDeuce);
        System.out.println("advantagePlayer: " + ongoingMatch.getAdvantage());

        assertFalse(isDeuce);
        assertNull(ongoingMatch.getAdvantage());
        System.out.println("Test passed\n");
    }

    @Test
    void shouldSetDeuceFlagTrueAndAdvantageIsNull() {
        System.out.println("=== shouldSetDeuceFlagTrueAndAdvantageIsNull ===");
        setPointsToDeuce();
        logScore("After setting to deuce");

        boolean isDeuce = service.isDeuce(score, player1, player2);

        System.out.println("isDeuce: " + isDeuce);
        System.out.println("advantagePlayer: " + ongoingMatch.getAdvantage());

        assertTrue(isDeuce);
        assertNull(ongoingMatch.getAdvantage());
        System.out.println("Test passed\n");
    }

    @Test
    void shouldSetAdvantageIfPlayerWinsPoint() {
        System.out.println("=== shouldSetAdvantageIfPlayerWinsPoint ===");
        setPointsToDeuce();
        logScore("After setting to deuce");

        service.calculate(ongoingMatch, player1);
        logScore("After player1 wins point");

        boolean isDeuce = service.isDeuce(score, player1, player2);
        System.out.println("isDeuce: " + isDeuce);
        System.out.println("advantagePlayer: " + ongoingMatch.getAdvantage());

        // После выигрыша очка при деусе игрок получает преимущество
        assertEquals(player1, ongoingMatch.getAdvantage());
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

        service.calculate(ongoingMatch, player1); // Получаем преимущество (41:40)
        logScore("After player1 gets advantage");

        service.calculate(ongoingMatch, player1); // Выигрываем гейм (42:40)
        logScore("After player1 wins game");

        boolean isDeuce = service.isDeuce(score, player1, player2);
        System.out.println("isDeuce: " + isDeuce);
        System.out.println("advantagePlayer: " + ongoingMatch.getAdvantage());

        // После выигрыша гейма очки сбрасываются
        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
        assertEquals(1, score.getGames(player1));
        assertEquals(0, score.getGames(player2));
        assertFalse(service.isDeuce(score, player1, player2));
        assertNull(ongoingMatch.getAdvantage());
        System.out.println("Test passed\n");
    }

    @Test
    void shouldResetAdvantageAndRollbackPointsIfPlayerWinsPoint() {
        System.out.println("=== shouldResetAdvantageAndRollbackPointsIfPlayerWinsPoint ===");
        setPointsToDeuce();
        logScore("After setting to deuce");

        service.calculate(ongoingMatch, player1); // player1 получает преимущество (41:40)
        logScore("After player1 gets advantage");

        service.calculate(ongoingMatch, player2); // player2 сравнивает
        logScore("After player2 equalizes");

        boolean isDeuce = service.isDeuce(score, player1, player2);
        System.out.println("isDeuce: " + isDeuce);
        System.out.println("advantagePlayer: " + ongoingMatch.getAdvantage());

        // Преимущество сбрасывается, возврат к деусу 40:40
        assertNull(ongoingMatch.getAdvantage());
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
        service.calculate(ongoingMatch, player1);
        logScore("After player1 gets advantage");
        System.out.println("advantagePlayer: " + ongoingMatch.getAdvantage());
        assertEquals(player1, ongoingMatch.getAdvantage());
        assertFalse(service.isDeuce(score, player1, player2)); // При преимуществе уже не деус

        // player2 сравнивает, возврат к деусу
        service.calculate(ongoingMatch, player2);
        logScore("After player2 equalizes");
        System.out.println("advantagePlayer: " + ongoingMatch.getAdvantage());
        assertNull(ongoingMatch.getAdvantage());
        assertTrue(service.isDeuce(score, player1, player2)); // Возврат к деусу 40:40

        // player2 получает преимущество
        service.calculate(ongoingMatch, player2);
        logScore("After player2 gets advantage");
        System.out.println("advantagePlayer: " + ongoingMatch.getAdvantage());
        assertEquals(player2, ongoingMatch.getAdvantage());
        assertFalse(service.isDeuce(score, player1, player2)); // При преимуществе уже не деус
        System.out.println("Test passed\n");
    }

    @Test
    void shouldMaintainPointsAt40DuringDeuce() {
        System.out.println("=== shouldMaintainPointsAt40DuringDeuce ===");
        setPointsToDeuce();
        logScore("After setting to deuce");

        service.calculate(ongoingMatch, player2);
        logScore("After player2 wins point");

        boolean isDeuce = service.isDeuce(score, player1, player2);
        System.out.println("isDeuce: " + isDeuce);

        // После получения преимущества очки остаются >= 40
        assertTrue(score.getPoints(player1) >= 40);
        assertTrue(score.getPoints(player2) >= 40);
        // Но это уже не деус, а преимущество
        assertFalse(isDeuce);
        // player2 должен иметь преимущество
        assertEquals(player2, ongoingMatch.getAdvantage());
        System.out.println("Test passed\n");
    }

    @Test
    void shouldTransitToTiebreakAfterDeuceGameAt6_6() {
        System.out.println("=== shouldTransitToTiebreakAfterDeuceGameAt6_6 ===");
        setGamesCount(5, 6);
        setPointsToDeuce();
        logScore("After setting games 5:6 and points to deuce");

        service.calculate(ongoingMatch, player1);
        logScore("After player1 gets advantage");

        service.calculate(ongoingMatch, player1);
        logScore("After player1 wins game");

        boolean isDeuce = service.isDeuce(score, player1, player2);
        System.out.println("isDeuce: " + isDeuce);
        System.out.println("isTieBreak: " + ongoingMatch.isTieBreak());
        System.out.println("advantagePlayer: " + ongoingMatch.getAdvantage());

        assertEquals(6, score.getGames(player1));
        assertEquals(6, score.getGames(player2));
        assertTrue(ongoingMatch.isTieBreak());
        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
        assertFalse(isDeuce);
        assertNull(ongoingMatch.getAdvantage());
        System.out.println("Test passed\n");
    }

    private void setPointsToDeuce() {
        score.setPoints(player1, 40);
        score.setPoints(player2, 40);
    }

    private void setGamesCount(int player1games, int player2games) {
        score.resetAllGames();

        for (int i = 0; i < player1games; i++) {
            score.awardGame(player1);
        }
        for (int i = 0; i < player2games; i++) {
            score.awardGame(player2);
        }
    }

    private void logScore(String moment) {
        System.out.println(moment + ":");
        System.out.println("  Points: " + score.getPoints(player1) + ":" + score.getPoints(player2));
        System.out.println("  Games: " + score.getGames(player1) + ":" + score.getGames(player2));
        System.out.println("  Sets: " + score.getSets(player1) + ":" + score.getSets(player2));
        System.out.println("  TieBreakPoints: " + score.getTieBreakPoints(player1) + ":" + score.getTieBreakPoints(player2));
        System.out.println("  IsTieBreak: " + ongoingMatch.isTieBreak());
        System.out.println("  AdvantagePlayer: " + ongoingMatch.getAdvantage());
    }
}
