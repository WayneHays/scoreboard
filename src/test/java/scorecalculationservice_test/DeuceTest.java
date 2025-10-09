package scorecalculationservice_test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeuceTest extends ScoreCalculationTestBase {

    @Test
    void shouldNotHaveAdvantageInitially() {
        assertNull(ongoingMatch.getAdvantage());
    }

    @Test
    void shouldNotHaveAdvantageAtDeuce() {
        setPointsToDeuce();

        assertNull(ongoingMatch.getAdvantage());
        assertEquals(40, ongoingMatch.getPoints(player1));
        assertEquals(40, ongoingMatch.getPoints(player2));
    }

    @Test
    void shouldSetAdvantageIfPlayerWinsPointAfterDeuce() {
        setPointsToDeuce();

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());

        assertEquals(player1, ongoingMatch.getAdvantage());
        assertEquals(41, ongoingMatch.getPoints(player1));
        assertEquals(40, ongoingMatch.getPoints(player2));
    }

    @Test
    void shouldWinGameIfPlayerHasAdvantageAndWinsAnotherPoint() {
        setPointsToDeuce();

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());

        assertEquals(0, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));
        assertEquals(1, ongoingMatch.getGames(player1));
        assertEquals(0, ongoingMatch.getGames(player2));
        assertNull(ongoingMatch.getAdvantage());
    }

    @Test
    void shouldReturnToDeuceIfOpponentWinsPointWhenPlayerHasAdvantage() {
        setPointsToDeuce();

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        service.awardPointToPlayer(ongoingMatch, player2.getId().toString());

        assertNull(ongoingMatch.getAdvantage());
        assertEquals(40, ongoingMatch.getPoints(player1));
        assertEquals(40, ongoingMatch.getPoints(player2));
    }

    @Test
    void shouldSwitchAdvantageBackAndForth() {
        setPointsToDeuce();

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        assertEquals(player1, ongoingMatch.getAdvantage());

        service.awardPointToPlayer(ongoingMatch, player2.getId().toString());
        assertNull(ongoingMatch.getAdvantage());

        service.awardPointToPlayer(ongoingMatch, player2.getId().toString());
        assertEquals(player2, ongoingMatch.getAdvantage());
    }

    @Test
    void shouldTransitToTiebreakAfterDeuceGameAt6_6() {
        setGamesCount(5, 6);
        setPointsToDeuce();

        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
        service.awardPointToPlayer(ongoingMatch, player1.getId().toString());

        assertEquals(6, ongoingMatch.getGames(player1));
        assertEquals(6, ongoingMatch.getGames(player2));
        assertTrue(ongoingMatch.isTieBreak());
        assertEquals(0, ongoingMatch.getPoints(player1));
        assertEquals(0, ongoingMatch.getPoints(player2));
        assertNull(ongoingMatch.getAdvantage());
    }

    private void setPointsToDeuce() {
        for (int i = 0; i < 3; i++) {
            service.awardPointToPlayer(ongoingMatch, player1.getId().toString());
            service.awardPointToPlayer(ongoingMatch, player2.getId().toString());
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
}
