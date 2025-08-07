package scorecalculationservice_test;

import com.scoreboard.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TieBreakTest extends ScoreCalculationTestBase {

    @Test
    void shouldStartTiebreakIfGamesCount6_6() {
        setGamesCount(5, 6);
        winCurrentGame(player1);

        assertTrue(score.isTieBreak());
    }

    @Test
    void shouldNotIncrementRegularPoints() {
        setGamesCount(5, 6);
        winCurrentGame(player1);

        service.calculate(matchWithScore, player1);
        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
    }

    @Test
    void shouldIncrementTiebreakPoints() {
        setGamesCount(5, 6);
        winCurrentGame(player1);

        service.calculate(matchWithScore, player1);
        assertEquals(1, score.getTieBreakPoints(player1));
    }

    @Test
    void shouldWinTiebreakWithSevenPointsAndTwoPointLead() {
        setGamesCount(6, 6);
        setTieBreakActive();

        setTieBreakPoints(20, 19);
        service.calculate(matchWithScore, player1);

        assertEquals(0, score.getGames(player1));
        assertEquals(1, score.getSets(player1));
        assertFalse(score.isTieBreak());
        assertFalse(score.isDeuce());
    }

    @Test
    void shouldFinishMatchIfTiebreakInThirdSet() {
        setSetsCount(1, 1);
        setGamesCount(6, 6);
        setTieBreakActive();
        setTieBreakPoints(6, 5);

        service.calculate(matchWithScore, player1);

        assertFalse(score.isTieBreak());
        assertFalse(score.isDeuce());
        assertTrue(score.isMatchFinished());
    }

    private void winCurrentGame(Player player) {
        for (int i = 0; i < 4; i++) {
            service.calculate(matchWithScore, player);
        }
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

    private void setTieBreakActive() {
        score.setTieBreak(true);
    }
}
