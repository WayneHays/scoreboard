package scorecalculationservice_test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class GameplayTest extends ScoreCalculationTestBase {

    @Test
    void shouldReturnIfMatchFinished() {
        // Устанавливаем счет через циклы
        score.awardSet(player1); // 1:0 сеты
        score.awardSet(player2); // 1:1 сеты

        // 5:0 игры
        for (int i = 0; i < 5; i++) {
            score.awardGame(player1);
        }

        // 40:0 очки
        score.setPoints(player1, 40);
        score.setPoints(player2, 0);

        service.calculate(ongoingMatch, player1);

        assertTrue(service.isMatchFinished(score, player1, player2));
        assertEquals(2, score.getSets(player1)); // Должно быть 2 сета для победы
        assertEquals(1, score.getSets(player2));
    }

    @Test
    void shouldIncrementCountOfPoints() {
        service.calculate(ongoingMatch, player1);
        assertEquals(15, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));

        service.calculate(ongoingMatch, player1);
        assertEquals(30, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));

        service.calculate(ongoingMatch, player1);
        assertEquals(40, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));

        service.calculate(ongoingMatch, player1);
        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
        assertEquals(1, score.getGames(player1)); // Должна быть засчитана игра
    }

    @Test
    void shouldIncrementCountOfGames() {
        service.calculate(ongoingMatch, player1);
        service.calculate(ongoingMatch, player1);
        service.calculate(ongoingMatch, player1);
        service.calculate(ongoingMatch, player1);

        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
        assertEquals(1, score.getGames(player1));
        assertEquals(0, score.getGames(player2));
    }
}
