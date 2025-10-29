package handler_test;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.ongoingmatch.OngoingMatch;
import com.scoreboard.service.scorecalculation.Points;
import com.scoreboard.service.scorecalculation.handler.TiebreakHandler;
import com.scoreboard.service.scorecalculation.rules.TiebreakRules;
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
            // Given: tiebreak is not active
            assertFalse(match.isTieBreak());

            // When: handler is called
            handler.handle(match, player1);

            // Then: no tiebreak points awarded
            assertEquals(0, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }

        @Test
        void handle_tiebreakNotActive_regularPointsUnchanged() {
            // Given: regular game in progress, no tiebreak
            match.awardPointTo(player1);
            match.awardPointTo(player1);
            Points initialPoints = match.getPoints(player1);

            // When: handler is called
            handler.handle(match, player1);

            // Then: regular points unchanged (handled by next handler)
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
            // Given: tiebreak active, 0-0

            // When: player1 scores
            handler.handle(match, player1);

            // Then: tiebreak points awarded
            assertEquals(1, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }

        @Test
        void handle_multiplePoints_shouldAccumulate() {
            // When: player1 scores multiple times
            handler.handle(match, player1); // 1-0
            handler.handle(match, player1); // 2-0
            handler.handle(match, player1); // 3-0

            // Then
            assertEquals(3, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }

        @Test
        void handle_bothPlayersScoring_shouldTrackSeparately() {
            // When: alternating scores
            handler.handle(match, player1); // 1-0
            handler.handle(match, player2); // 1-1
            handler.handle(match, player1); // 2-1
            handler.handle(match, player2); // 2-2
            handler.handle(match, player1); // 3-2

            // Then
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
            // Given: 0-0

            // When: player1 scores 7 points
            for (int i = 0; i < 7; i++) {
                handler.handle(match, player1);
            }

            // Then: tiebreak won, game awarded
            assertEquals(1, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertFalse(match.isTieBreak());
            assertEquals(0, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }

        @Test
        void handle_7to5_shouldWinTiebreak() {
            // Given: play to 6-5 score
            playTiebreakToScore(6, 5);

            // When: player1 scores (7-5)
            handler.handle(match, player1);

            // Then: tiebreak won
            assertEquals(1, match.getGames(player1));
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_7to3_shouldWinTiebreak() {
            // Given: play to 6-3 score
            playTiebreakToScore(6, 3);

            // When: player1 scores (7-3)
            handler.handle(match, player1);

            // Then: tiebreak won
            assertEquals(1, match.getGames(player1));
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_player2Wins7to4() {
            // Given: play to 4-6 score
            playTiebreakToScore(4, 6);

            // When: player2 scores (4-7)
            handler.handle(match, player2);

            // Then: tiebreak won by player2
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
            // Given: play to 6-6 score
            playTiebreakToScore(6, 6);

            // When: check state
            // Then: tiebreak continues (need 2-point lead)
            assertTrue(match.isTieBreak());
            assertEquals(6, match.getTieBreakPoints(player1));
            assertEquals(6, match.getTieBreakPoints(player2));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
        }

        @Test
        void handle_7to6_shouldNotFinish() {
            // Given: play to 6-6
            playTiebreakToScore(6, 6);

            // When: player1 scores (7-6)
            handler.handle(match, player1);

            // Then: tiebreak continues (only 1-point lead)
            assertTrue(match.isTieBreak());
            assertEquals(7, match.getTieBreakPoints(player1));
            assertEquals(6, match.getTieBreakPoints(player2));
            assertEquals(0, match.getGames(player1));
        }

        @Test
        void handle_8to6_shouldFinish() {
            // Given: play to 7-6 score
            playTiebreakToScore(7, 6);

            // When: player1 scores (8-6)
            handler.handle(match, player1);

            // Then: tiebreak won (2-point lead)
            assertEquals(1, match.getGames(player1));
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_10to8_shouldFinish() {
            // Given: play to 9-8 score
            playTiebreakToScore(9, 8);

            // When: player1 scores (10-8)
            handler.handle(match, player1);

            // Then: tiebreak won
            assertEquals(1, match.getGames(player1));
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_15to13_shouldFinish() {
            // Given: play to 14-13 score
            playTiebreakToScore(14, 13);

            // When: player1 scores (15-13)
            handler.handle(match, player1);

            // Then: tiebreak won
            assertEquals(1, match.getGames(player1));
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_longTiebreak_multipleLeadChanges() {
            // Simulate long tiebreak: 6-6, 7-6, 7-7, 8-7, 8-8, 9-8, 9-9, 10-9, 11-9
            playTiebreakToScore(6, 6);

            handler.handle(match, player1); // 7-6
            assertTrue(match.isTieBreak());

            handler.handle(match, player2); // 7-7
            assertTrue(match.isTieBreak());

            handler.handle(match, player1); // 8-7
            assertTrue(match.isTieBreak());

            handler.handle(match, player2); // 8-8
            assertTrue(match.isTieBreak());

            handler.handle(match, player1); // 9-8
            assertTrue(match.isTieBreak());

            handler.handle(match, player2); // 9-9
            assertTrue(match.isTieBreak());

            handler.handle(match, player1); // 10-9
            assertTrue(match.isTieBreak());

            handler.handle(match, player1); // 11-9 - WIN
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
            // Player1 wins 7 straight points
            for (int i = 0; i < 7; i++) {
                handler.handle(match, player1);
            }

            assertEquals(1, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertFalse(match.isTieBreak());
        }

        @Test
        void completeTiebreak_7to5() {
            // Scenario: 0-0 → 1-0 → 1-1 → 2-1 → 2-2 → 3-2 → 3-3 → 4-3 → 5-3 → 6-3 → 6-4 → 6-5 → 7-5
            handler.handle(match, player1); // 1-0
            handler.handle(match, player2); // 1-1
            handler.handle(match, player1); // 2-1
            handler.handle(match, player2); // 2-2
            handler.handle(match, player1); // 3-2
            handler.handle(match, player2); // 3-3
            handler.handle(match, player1); // 4-3
            handler.handle(match, player1); // 5-3
            handler.handle(match, player1); // 6-3
            handler.handle(match, player2); // 6-4
            handler.handle(match, player2); // 6-5

            assertTrue(match.isTieBreak()); // Still active

            handler.handle(match, player1); // 7-5 - WIN

            assertFalse(match.isTieBreak());
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void completeTiebreak_8to6() {
            // Play to 6-6
            for (int i = 0; i < 6; i++) {
                handler.handle(match, player1);
                handler.handle(match, player2);
            }

            assertEquals(6, match.getTieBreakPoints(player1));
            assertEquals(6, match.getTieBreakPoints(player2));

            // 7-6
            handler.handle(match, player1);
            assertTrue(match.isTieBreak()); // Still going

            // 8-6 - WIN
            handler.handle(match, player1);
            assertFalse(match.isTieBreak());
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void completeTiebreak_player2Wins() {
            // Player2 wins 7-4
            playTiebreakToScore(4, 6);
            handler.handle(match, player2); // 4-7

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
            // Setup: 6-6 games before tiebreak
            for (int i = 0; i < 6; i++) {
                match.awardGameTo(player1);
                match.awardGameTo(player2);
            }
            match.setTieBreak(true);
        }

        @Test
        void handle_afterTiebreakWin_shouldAwardGame() {
            // Given: 6-6 in games, play tiebreak to 6-5
            playTiebreakToScore(6, 5);

            // When: player1 wins tiebreak 7-5
            handler.handle(match, player1);

            // Then: games should be 7-6
            assertEquals(7, match.getGames(player1));
            assertEquals(6, match.getGames(player2));
        }

        @Test
        void handle_afterTiebreakWin_shouldResetTiebreakPoints() {
            // When: player1 wins tiebreak 7-0
            for (int i = 0; i < 7; i++) {
                handler.handle(match, player1);
            }

            // Then: tiebreak points reset
            assertEquals(0, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }

        @Test
        void handle_afterTiebreakWin_shouldDisableTiebreakMode() {
            // When: player1 wins tiebreak
            for (int i = 0; i < 7; i++) {
                handler.handle(match, player1);
            }

            // Then: tiebreak mode disabled
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_afterTiebreakWin_regularPointsShouldBeZero() {
            // When: tiebreak won
            for (int i = 0; i < 7; i++) {
                handler.handle(match, player1);
            }

            // Then: regular points at zero
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
            // Given: match is finished
            match.setWinner(player1);

            // When: try to award tiebreak point
            handler.handle(match, player1);

            // Then: no points awarded
            assertEquals(0, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }

        @Test
        void handle_matchFinished_tiebreakStateUnchanged() {
            // Given: match finished with tiebreak active
            match.setWinner(player2);
            assertTrue(match.isTieBreak());

            // When: multiple attempts
            handler.handle(match, player1);
            handler.handle(match, player1);

            // Then: state unchanged
            assertTrue(match.isTieBreak()); // Still marked as tiebreak (though match is over)
            assertEquals(0, match.getTieBreakPoints(player1));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        void handle_tiebreakJustActivated_shouldStartFrom0() {
            // Given: tiebreak just activated
            match.setTieBreak(true);

            // When: first point
            handler.handle(match, player1);

            // Then: 1-0
            assertEquals(1, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }

        @Test
        void handle_minimumPointsNotReached_shouldNotFinish() {
            // Given: tiebreak active
            match.setTieBreak(true);

            // When: player1 has 6 points, player2 has 0
            for (int i = 0; i < 6; i++) {
                handler.handle(match, player1);
            }

            // Then: still active (need 7 minimum)
            assertTrue(match.isTieBreak());
            assertEquals(6, match.getTieBreakPoints(player1));
            assertEquals(0, match.getGames(player1));
        }

        @Test
        void handle_exactlyMinimumWith2PointLead_shouldFinish() {
            // Given: tiebreak active
            match.setTieBreak(true);

            // Play to 7-3
            handler.handle(match, player1); // 1-0
            handler.handle(match, player1); // 2-0
            handler.handle(match, player1); // 3-0
            handler.handle(match, player2); // 3-1
            handler.handle(match, player2); // 3-2
            handler.handle(match, player2); // 3-3
            handler.handle(match, player1); // 4-3
            handler.handle(match, player1); // 5-3
            handler.handle(match, player1); // 6-3 (not finished yet)

            assertTrue(match.isTieBreak());
            assertEquals(6, match.getTieBreakPoints(player1));

            handler.handle(match, player1); // 7-3 - WIN

            // Then: finished
            assertFalse(match.isTieBreak());
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void handle_highScore_shouldStillRespectRules() {
            // Given: tiebreak active
            match.setTieBreak(true);

            // Play to 6-6 first (alternate points)
            for (int i = 0; i < 6; i++) {
                handler.handle(match, player1);
                handler.handle(match, player2);
            }

            // Continue alternating to 19-19
            for (int i = 6; i < 19; i++) {
                handler.handle(match, player1);
                handler.handle(match, player2);
            }

            // Now 19-19, player1 scores to 20-19
            handler.handle(match, player1);

            assertEquals(20, match.getTieBreakPoints(player1));
            assertEquals(19, match.getTieBreakPoints(player2));
            assertTrue(match.isTieBreak()); // Only 1-point lead

            // When: player1 scores (21-19) - 2-point lead
            handler.handle(match, player1);

            // Then: finished
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
            // Scenario: 1-0, 1-1, 2-1, 2-2, 3-2, 3-3, 4-3, 4-4, 5-4, 5-5, 6-5, 6-6, 7-6, 8-6
            Player[] scorers = {
                    player1, player2, player1, player2, player1, player2,
                    player1, player2, player1, player2, player1, player2
            };

            for (Player scorer : scorers) {
                handler.handle(match, scorer);
                assertTrue(match.isTieBreak()); // Should still be active
            }

            assertEquals(6, match.getTieBreakPoints(player1));
            assertEquals(6, match.getTieBreakPoints(player2));

            // Last two points
            handler.handle(match, player1); // 7-6
            assertTrue(match.isTieBreak()); // Still active (only 1-point lead)

            handler.handle(match, player1); // 8-6
            assertFalse(match.isTieBreak()); // Finished
            assertEquals(1, match.getGames(player1));
        }
    }

    // Helper methods
    private void playTiebreakToScore(int player1Points, int player2Points) {
        // Play tiebreak by using handler.handle() to reach desired score
        int minPoints = Math.min(player1Points, player2Points);

        // First alternate points up to minimum
        for (int i = 0; i < minPoints; i++) {
            handler.handle(match, player1);
            handler.handle(match, player2);
        }

        // Then add remaining points to the leader
        int remainingPlayer1 = player1Points - minPoints;
        int remainingPlayer2 = player2Points - minPoints;

        for (int i = 0; i < remainingPlayer1; i++) {
            handler.handle(match, player1);
        }

        for (int i = 0; i < remainingPlayer2; i++) {
            handler.handle(match, player2);
        }
    }

    // Standard tiebreak rules implementation
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