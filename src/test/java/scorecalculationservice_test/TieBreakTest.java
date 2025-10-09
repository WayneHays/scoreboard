package scorecalculationservice_test;

import com.scoreboard.model.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TieBreakTest extends ScoreCalculationTestBase {

    @Test
    void shouldStartTiebreakIfGamesCount6_6() {
        setGamesScore(5, 6);

        winCurrentGame(player1);

        assertTrue(ongoingMatch.isTieBreak());
    }

    @Test
    void shouldNotIncrementRegularPointsInTiebreak() {
        setGamesScore(5, 6);
        winCurrentGame(player1);

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());

        assertEquals(0, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));
    }

    @Test
    void shouldIncrementTiebreakPoints() {
        setGamesScore(5, 6);
        winCurrentGame(player1);

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());

        assertEquals(1, ongoingMatch.getTieBreakPoints(player1));
    }

    @Test
    void shouldWinTiebreakWithSevenPointsAndTwoPointLead() {
        setGamesScore(6, 6);
        setTieBreakPointsBeforeMatchServe();
        ongoingMatch.setTieBreak(true);

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());

        assertEquals(0, ongoingMatch.getGames(player1));
        assertEquals(1, ongoingMatch.getSets(player1));
        assertFalse(ongoingMatch.isTieBreak());
        assertNull(ongoingMatch.getAdvantage());
    }

    @Test
    void shouldFinishMatchIfTiebreakInThirdSet() {
        setDrawBySets();
        setGamesScore(6, 6);
        setTieBreakPointsBeforeMatchServe();
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

    private void setGamesScore(int player1games, int player2games) {
        ongoingMatch.resetAllGames();

        for (int i = 0; i < player1games; i++) {
            ongoingMatch.awardGame(player1);
        }
        for (int i = 0; i < player2games; i++) {
            ongoingMatch.awardGame(player2);
        }
    }

    private void setDrawBySets() {
        for (int i = 0; i < 1; i++) {
            ongoingMatch.awardSet(player1);
        }
        for (int i = 0; i < 1; i++) {
            ongoingMatch.awardSet(player2);
        }
    }

    private void setTieBreakPointsBeforeMatchServe() {
        for (int i = 0; i < 6; i++) {
            ongoingMatch.awardTieBreakPoint(player1);
        }
        for (int i = 0; i < 5; i++) {
            ongoingMatch.awardTieBreakPoint(player2);
        }
    }
}
