package scorecalculationservice_test;

import com.scoreboard.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TieBreakTest extends ScoreCalculationTestBase {

    @Test
    void shouldStartTiebreakIfGamesCount6_6() {
        setGamesCount(5, 6);
        winCurrentGame(player1);

        assertTrue(ongoingMatch.isTieBreak());
    }

    @Test
    void shouldNotIncrementRegularPoints() {
        setGamesCount(5, 6);
        winCurrentGame(player1);

        service.calculate(ongoingMatch, player1);
        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
    }

    @Test
    void shouldIncrementTiebreakPoints() {
        setGamesCount(5, 6);
        winCurrentGame(player1);

        service.calculate(ongoingMatch, player1);
        assertEquals(1, score.getTieBreakPoints(player1));
    }

    @Test
    void shouldWinTiebreakWithSevenPointsAndTwoPointLead() {
        setGamesCount(6, 6);
        setTieBreakPoints(20, 19);
        ongoingMatch.setTieBreak(true);

        System.out.println("Before calculate:");
        System.out.println("  TieBreakPoints: " + score.getTieBreakPoints(player1) + ":" + score.getTieBreakPoints(player2));
        System.out.println("  Games: " + score.getGames(player1) + ":" + score.getGames(player2));
        System.out.println("  Sets: " + score.getSets(player1) + ":" + score.getSets(player2));

        service.calculate(ongoingMatch, player1);

        System.out.println("After calculate:");
        System.out.println("  TieBreakPoints: " + score.getTieBreakPoints(player1) + ":" + score.getTieBreakPoints(player2));
        System.out.println("  Games: " + score.getGames(player1) + ":" + score.getGames(player2));
        System.out.println("  Sets: " + score.getSets(player1) + ":" + score.getSets(player2));
        System.out.println("  IsTieBreak: " + ongoingMatch.isTieBreak());

        assertEquals(0, score.getGames(player1));
        assertEquals(1, score.getSets(player1));
        assertFalse(ongoingMatch.isTieBreak());
        assertNull(ongoingMatch.getAdvantage());
    }

    @Test
    void shouldFinishMatchIfTiebreakInThirdSet() {
        setSetsCount(1, 1);
        setGamesCount(6, 6);
        setTieBreakPoints(6, 5);
        ongoingMatch.setTieBreak(true); // Устанавливаем тай-брейк вручную

        service.calculate(ongoingMatch, player1);

        assertFalse(ongoingMatch.isTieBreak());
        assertNull(ongoingMatch.getAdvantage());
        assertTrue(service.isMatchFinished(score, player1, player2));
    }

    private void winCurrentGame(Player player) {
        for (int i = 0; i < 4; i++) {
            service.calculate(ongoingMatch, player);
        }
    }

    private void setGamesCount(int player1games, int player2games) {
        // Сбрасываем текущие игры и устанавливаем новые через циклы
        score.resetAllGames();

        for (int i = 0; i < player1games; i++) {
            score.awardGame(player1);
        }
        for (int i = 0; i < player2games; i++) {
            score.awardGame(player2);
        }
    }

    private void setSetsCount(int player1sets, int player2sets) {
        // Устанавливаем сеты через циклы
        for (int i = 0; i < player1sets; i++) {
            score.awardSet(player1);
        }
        for (int i = 0; i < player2sets; i++) {
            score.awardSet(player2);
        }
    }

    private void setTieBreakPoints(int player1Points, int player2Points) {
        // Устанавливаем тай-брейк очки через циклы
        for (int i = 0; i < player1Points; i++) {
            score.awardTieBreakPoint(player1);
        }
        for (int i = 0; i < player2Points; i++) {
            score.awardTieBreakPoint(player2);
        }
    }
}
