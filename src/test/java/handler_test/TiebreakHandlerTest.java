package handler_test;

import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.model.entity.Player;
import com.scoreboard.service.scorecalculation.Points;
import com.scoreboard.service.scorecalculation.handler.TiebreakHandler;
import com.scoreboard.tennisrules.TiebreakRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TiebreakHandlerTest {
    private Player player1;
    private Player player2;
    private OngoingMatch match;
    private TiebreakHandler handler;
    private TiebreakRules tiebreakRules;

    @BeforeEach
    void setUp() {
        player1 = new Player("Roger Federer");
        player2 = new Player("Rafael Nadal");
        match = new OngoingMatch(player1, player2);
        tiebreakRules = new StandardTiebreakRules();
        handler = new TiebreakHandler(tiebreakRules);
    }

    @Nested
    @DisplayName("Tiebreak Not Active Tests")
    class TiebreakNotActiveTests {

        @Test
        void handle_tiebreakNotActive_shouldNotProcessPoints() {
            assertFalse(match.isTieBreak());

            handler.handle(match, player1);

            assertEquals(0, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }

        @Test
        void handle_tiebreakNotActive_regularPointsUnchanged() {
            match.awardPointTo(player1);
            match.awardPointTo(player1);
            Points initialPoints = match.getPoints(player1);

            handler.handle(match, player1);

            assertEquals(initialPoints, match.getPoints(player1));
        }
    }

    @Nested
    @DisplayName("Tiebreak Point Award Tests")
    class TiebreakPointAwardTests {

        @BeforeEach
        void activateTiebreak() {
            match.setTieBreak(true);
        }

        @Test
        void handle_tiebreakActive_shouldAwardTiebreakPoint() {
            handler.handle(match, player1);

            assertEquals(1, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }

        @Test
        void handle_multiplePoints_shouldAccumulate() {
            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player1);

            assertEquals(3, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }

        @Test
        void handle_bothPlayersScoring_shouldTrackSeparately() {
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player1);

            assertEquals(3, match.getTieBreakPoints(player1));
            assertEquals(2, match.getTieBreakPoints(player2));
        }
    }

    @Nested
    @DisplayName("Tiebreak Win Tests - Standard 7 Points")
    class StandardTiebreakWinTests {

        @BeforeEach
        void activateTiebreak() {
            match.setTieBreak(true);
        }

        @Test
        void handle_7to0_shouldWinTiebreak() {
            for (int i = 0; i < 7; i++) {
                handler.handle(match, player1);
            }

            assertEquals(1, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertFalse(match.isTieBreak());
            assertEquals(0, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }

        @Test
        void handle_7to5_shouldWinTiebreak() {
            playTiebreakToScore(6, 5);

            handler.handle(match, player1);

            assertEquals(1, match.getGames(player1));
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_7to3_shouldWinTiebreak() {
            playTiebreakToScore(6, 3);

            handler.handle(match, player1);

            assertEquals(1, match.getGames(player1));
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_player2Wins7to4() {
            playTiebreakToScore(4, 6);

            handler.handle(match, player2);

            assertEquals(0, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
            assertFalse(match.isTieBreak());
        }
    }

    @Nested
    @DisplayName("Tiebreak Extended Play Tests")
    class ExtendedTiebreakTests {

        @BeforeEach
        void activateTiebreak() {
            match.setTieBreak(true);
        }

        @Test
        void handle_6to6_shouldNotFinish() {
            playTiebreakToScore(6, 6);

            assertTrue(match.isTieBreak());
            assertEquals(6, match.getTieBreakPoints(player1));
            assertEquals(6, match.getTieBreakPoints(player2));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
        }

        @Test
        void handle_7to6_shouldNotFinish() {
            playTiebreakToScore(6, 6);

            handler.handle(match, player1);

            assertTrue(match.isTieBreak());
            assertEquals(7, match.getTieBreakPoints(player1));
            assertEquals(6, match.getTieBreakPoints(player2));
            assertEquals(0, match.getGames(player1));
        }

        @Test
        void handle_8to6_shouldFinish() {
            playTiebreakToScore(7, 6);

            handler.handle(match, player1);

            assertEquals(1, match.getGames(player1));
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_10to8_shouldFinish() {
            playTiebreakToScore(9, 8);

            handler.handle(match, player1);

            assertEquals(1, match.getGames(player1));
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_15to13_shouldFinish() {
            playTiebreakToScore(14, 13);

            handler.handle(match, player1);

            assertEquals(1, match.getGames(player1));
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_longTiebreak_multipleLeadChanges() {
            playTiebreakToScore(6, 6);

            handler.handle(match, player1);
            assertTrue(match.isTieBreak());

            handler.handle(match, player2);
            assertTrue(match.isTieBreak());

            handler.handle(match, player1);
            assertTrue(match.isTieBreak());

            handler.handle(match, player2);
            assertTrue(match.isTieBreak());

            handler.handle(match, player1);
            assertTrue(match.isTieBreak());

            handler.handle(match, player2);
            assertTrue(match.isTieBreak());

            handler.handle(match, player1);
            assertTrue(match.isTieBreak());

            handler.handle(match, player1);
            assertFalse(match.isTieBreak());
            assertEquals(1, match.getGames(player1));
        }
    }

    @Nested
    @DisplayName("Complete Tiebreak Scenarios")
    class CompleteTiebreakScenarioTests {

        @BeforeEach
        void activateTiebreak() {
            match.setTieBreak(true);
        }

        @Test
        void completeTiebreak_straightWin7to0() {
            for (int i = 0; i < 7; i++) {
                handler.handle(match, player1);
            }

            assertEquals(1, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertFalse(match.isTieBreak());
        }

        @Test
        void completeTiebreak_7to5() {
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player2);

            assertTrue(match.isTieBreak());

            handler.handle(match, player1);

            assertFalse(match.isTieBreak());
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void completeTiebreak_8to6() {
            for (int i = 0; i < 6; i++) {
                handler.handle(match, player1);
                handler.handle(match, player2);
            }

            assertEquals(6, match.getTieBreakPoints(player1));
            assertEquals(6, match.getTieBreakPoints(player2));

            handler.handle(match, player1);
            assertTrue(match.isTieBreak());

            handler.handle(match, player1);
            assertFalse(match.isTieBreak());
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void completeTiebreak_player2Wins() {
            playTiebreakToScore(4, 6);
            handler.handle(match, player2);

            assertEquals(0, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
            assertFalse(match.isTieBreak());
        }
    }

    @Nested
    @DisplayName("State Changes After Tiebreak")
    class StateChangesTests {

        @BeforeEach
        void setupGamesAndActivateTiebreak() {
            for (int i = 0; i < 6; i++) {
                match.awardGameTo(player1);
                match.awardGameTo(player2);
            }
            match.setTieBreak(true);
        }

        @Test
        void handle_afterTiebreakWin_shouldAwardGame() {
            playTiebreakToScore(6, 5);

            handler.handle(match, player1);

            assertEquals(7, match.getGames(player1));
            assertEquals(6, match.getGames(player2));
        }

        @Test
        void handle_afterTiebreakWin_shouldResetTiebreakPoints() {
            for (int i = 0; i < 7; i++) {
                handler.handle(match, player1);
            }

            assertEquals(0, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }

        @Test
        void handle_afterTiebreakWin_shouldDisableTiebreakMode() {
            for (int i = 0; i < 7; i++) {
                handler.handle(match, player1);
            }

            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_afterTiebreakWin_regularPointsShouldBeZero() {
            for (int i = 0; i < 7; i++) {
                handler.handle(match, player1);
            }

            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }
    }

    @Nested
    @DisplayName("Match Finished Tests")
    class MatchFinishedTests {

        @BeforeEach
        void activateTiebreak() {
            match.setTieBreak(true);
        }

        @Test
        void handle_matchAlreadyFinished_shouldNotProcessPoints() {
            match.setWinner(player1);

            handler.handle(match, player1);

            assertEquals(0, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }

        @Test
        void handle_matchFinished_tiebreakStateUnchanged() {
            match.setWinner(player2);
            assertTrue(match.isTieBreak());

            handler.handle(match, player1);
            handler.handle(match, player1);

            assertTrue(match.isTieBreak());
            assertEquals(0, match.getTieBreakPoints(player1));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        void handle_tiebreakJustActivated_shouldStartFrom0() {
            match.setTieBreak(true);

            handler.handle(match, player1);

            assertEquals(1, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }

        @Test
        void handle_minimumPointsNotReached_shouldNotFinish() {
            match.setTieBreak(true);

            for (int i = 0; i < 6; i++) {
                handler.handle(match, player1);
            }

            assertTrue(match.isTieBreak());
            assertEquals(6, match.getTieBreakPoints(player1));
            assertEquals(0, match.getGames(player1));
        }

        @Test
        void handle_exactlyMinimumWith2PointLead_shouldFinish() {
            match.setTieBreak(true);

            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player2);
            handler.handle(match, player2);
            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player1);

            assertTrue(match.isTieBreak());
            assertEquals(6, match.getTieBreakPoints(player1));

            handler.handle(match, player1);

            assertFalse(match.isTieBreak());
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void handle_highScore_shouldStillRespectRules() {
            match.setTieBreak(true);

            for (int i = 0; i < 6; i++) {
                handler.handle(match, player1);
                handler.handle(match, player2);
            }

            for (int i = 6; i < 19; i++) {
                handler.handle(match, player1);
                handler.handle(match, player2);
            }

            handler.handle(match, player1);

            assertEquals(20, match.getTieBreakPoints(player1));
            assertEquals(19, match.getTieBreakPoints(player2));
            assertTrue(match.isTieBreak());

            handler.handle(match, player1);

            assertFalse(match.isTieBreak());
            assertEquals(1, match.getGames(player1));
        }
    }

    @Nested
    @DisplayName("Alternating Scenarios")
    class AlternatingScenariosTests {

        @BeforeEach
        void activateTiebreak() {
            match.setTieBreak(true);
        }

        @Test
        void handle_backAndForth_shouldTrackCorrectly() {
            Player[] scorers = {
                    player1, player2, player1, player2, player1, player2,
                    player1, player2, player1, player2, player1, player2
            };

            for (Player scorer : scorers) {
                handler.handle(match, scorer);
                assertTrue(match.isTieBreak());
            }

            assertEquals(6, match.getTieBreakPoints(player1));
            assertEquals(6, match.getTieBreakPoints(player2));

            handler.handle(match, player1);
            assertTrue(match.isTieBreak());

            handler.handle(match, player1);
            assertFalse(match.isTieBreak());
            assertEquals(1, match.getGames(player1));
        }
    }

    private void playTiebreakToScore(int player1Points, int player2Points) {
        int minPoints = Math.min(player1Points, player2Points);

        for (int i = 0; i < minPoints; i++) {
            handler.handle(match, player1);
            handler.handle(match, player2);
        }

        int remainingPlayer1 = player1Points - minPoints;
        int remainingPlayer2 = player2Points - minPoints;

        for (int i = 0; i < remainingPlayer1; i++) {
            handler.handle(match, player1);
        }

        for (int i = 0; i < remainingPlayer2; i++) {
            handler.handle(match, player2);
        }
    }

    private static class StandardTiebreakRules implements TiebreakRules {
        @Override
        public int pointsToWinTieBreak() {
            return 7;
        }

        @Override
        public int minDifferenceToWinTieBreak() {
            return 2;
        }
    }
}