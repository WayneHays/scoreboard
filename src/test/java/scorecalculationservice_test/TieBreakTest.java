package scorecalculationservice_test;

import com.scoreboard.model.GameState;
import com.scoreboard.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TieBreakTest extends ScoreCalculationTestBase {

    @Test
    void shouldStartTiebreakIfGamesCount6_6() {
        setGamesCount(5, 6);
        GameState gameState = winCurrentGame(player1);

        assertTrue(gameState.isTieBreak());
    }

    @Test
    void shouldNotIncrementRegularPoints() {
        setGamesCount(5, 6);
        winCurrentGame(player1);

        service.calculate(match, score, player1);
        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
    }

    @Test
    void shouldIncrementTiebreakPoints() {
        setGamesCount(5, 6);
        winCurrentGame(player1);

        service.calculate(match, score, player1);
        assertEquals(1, score.getTieBreakPoints(player1));
    }

    @Test
    void shouldWinTiebreakWithSevenPointsAndTwoPointLead() {
        setGamesCount(6, 6);
        setTieBreakPoints(20, 19);

        GameState gameState = service.calculate(match, score, player1);

        assertEquals(0, score.getGames(player1));
        assertEquals(1, score.getSets(player1));
        assertFalse(gameState.isTieBreak());
        assertNull(gameState.advantagePlayer());
    }

    @Test
    void shouldFinishMatchIfTiebreakInThirdSet() {
        setSetsCount(1, 1);
        setGamesCount(6, 6);
        setTieBreakPoints(6, 5);

        GameState gameState = service.calculate(match, score, player1);

        assertFalse(gameState.isTieBreak());
        assertNull(gameState.advantagePlayer());
        assertTrue(service.isMatchFinished(score, player1, player2));
    }

    private GameState winCurrentGame(Player player) {
        GameState gameState = null;
        for (int i = 0; i < 4; i++) {
            gameState = service.calculate(match, score, player);
        }
        return gameState;
    }

    private void setGamesCount(int player1games, int player2games) {
        score.setGames(player1, player1games);
        score.setGames(player2, player2games);
    }

    private void setSetsCount(int player1sets, int player2sets) {
        score.setSets(player1, player1sets);
        score.setSets(player2, player2sets);
    }

    private void setTieBreakPoints(int player1Points, int player2Points) {
        score.setTieBreakPoints(player1, player1Points);
        score.setTieBreakPoints(player2, player2Points);
    }
}
