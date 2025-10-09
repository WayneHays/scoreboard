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
    void shouldNotIncrementRegularPointsInTiebreak() {
        setGamesCount(5, 6);
        winCurrentGame(player1);

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        assertEquals(0, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));
    }

    @Test
    void shouldIncrementTiebreakPoints() {
        setGamesCount(5, 6);
        winCurrentGame(player1);

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        assertEquals(1, ongoingMatch.getTieBreakPoints(player1));
    }

    @Test
    void shouldWinTiebreakWithSevenPointsAndTwoPointLead() {
        setGamesCount(6, 6);
        setTieBreakPoints(6, 5);
        ongoingMatch.setTieBreak(true);

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());

        assertEquals(0, ongoingMatch.getGames(player1));
        assertEquals(1, ongoingMatch.getSets(player1));
        assertFalse(ongoingMatch.isTieBreak());
        assertNull(ongoingMatch.getAdvantage());
    }

    @Test
    void shouldFinishMatchIfTiebreakInThirdSet() {
        setSetsCount(1, 1);
        setGamesCount(6, 6);
        setTieBreakPoints(6, 5);
        ongoingMatch.setTieBreak(true);

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());

        assertFalse(ongoingMatch.isTieBreak());
        assertNull(ongoingMatch.getAdvantage());
        assertTrue(service.isMatchFinished(ongoingMatch));
    }

    private void winCurrentGame(Player player) {
        for (int i = 0; i < 4; i++) {
            service.awardPointToPlayer(ongoingMatch, player.getId().toString());
        }
    }

    private void setGamesCount(int player1games, int player2games) {
        ongoingMatch.resetAllGames();

        for (int i = 0; i < player1games; i++) {
            ongoingMatch.awardGame(player1);
        }
        for (int i = 0; i < player2games; i++) {
            ongoingMatch.awardGame(player2);
        }
    }

    private void setSetsCount(int player1sets, int player2sets) {
        for (int i = 0; i < player1sets; i++) {
            ongoingMatch.awardSet(player1);
        }
        for (int i = 0; i < player2sets; i++) {
            ongoingMatch.awardSet(player2);
        }
    }

    private void setTieBreakPoints(int player1Points, int player2Points) {
        for (int i = 0; i < player1Points; i++) {
            ongoingMatch.awardTieBreakPoint(player1);
        }
        for (int i = 0; i < player2Points; i++) {
            ongoingMatch.awardTieBreakPoint(player2);
        }
    }
}
