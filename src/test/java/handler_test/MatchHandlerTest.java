package handler_test;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.service.scorecalculation.Points;
import com.scoreboard.service.scorecalculation.handler.MatchHandler;
import com.scoreboard.service.scorecalculation.rules.MatchRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchHandlerTest {
    private Player player1;
    private Player player2;
    private OngoingMatch match;
    private MatchHandler handler;
    private MatchRules matchRules;

    @BeforeEach
    void setUp() {
        player1 = new Player("Rafael Nadal");
        player2 = new Player("Roger Federer");
        match = new OngoingMatch(player1, player2);
        matchRules = new BestOfThreeMatchRules();
        handler = new MatchHandler(matchRules);
    }

    @Nested
    @DisplayName("Match Win Tests - Best of 3")
    class BestOfThreeMatchWinTests {

        @Test
        void handle_2to0_shouldWinMatch() {
            // Given: player1 has 2 sets, player2 has 0
            awardSets(player1, 2);

            // When
            handler.handle(match, player1);

            // Then: match won
            assertEquals(player1, match.getWinner());
            assertTrue(match.isFinished());
        }

        @Test
        void handle_2to1_shouldWinMatch() {
            // Given: player1 has 2 sets, player2 has 1
            awardSets(player1, 2);
            awardSets(player2, 1);

            // When
            handler.handle(match, player1);

            // Then: match won
            assertEquals(player1, match.getWinner());
            assertTrue(match.isFinished());
        }

        @Test
        void handle_0to2_player2Wins() {
            // Given: player2 has 2 sets
            awardSets(player2, 2);

            // When
            handler.handle(match, player2);

            // Then: player2 wins match
            assertEquals(player2, match.getWinner());
            assertTrue(match.isFinished());
        }

        @Test
        void handle_1to2_player2Wins() {
            // Given: 1-2 in sets
            awardSets(player1, 1);
            awardSets(player2, 2);

            // When
            handler.handle(match, player2);

            // Then: player2 wins
            assertEquals(player2, match.getWinner());
            assertTrue(match.isFinished());
        }
    }

    @Nested
    @DisplayName("Match Not Won Tests - Best of 3")
    class BestOfThreeMatchNotWonTests {

        @Test
        void handle_0to0_shouldNotWinMatch() {
            // Given: 0-0 in sets

            // When
            handler.handle(match, player1);

            // Then: no winner
            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }

        @Test
        void handle_1to0_shouldNotWinMatch() {
            // Given: 1-0 in sets
            awardSets(player1, 1);

            // When
            handler.handle(match, player1);

            // Then: no winner (need 2 sets)
            assertNull(match.getWinner());
            assertFalse(match.isFinished());
            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_1to1_shouldNotWinMatch() {
            // Given: 1-1 in sets
            awardSets(player1, 1);
            awardSets(player2, 1);

            // When
            handler.handle(match, player1);

            // Then: no winner (tied)
            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }

        @Test
        void handle_0to1_shouldNotWinMatch() {
            // Given: 0-1 in sets
            awardSets(player2, 1);

            // When
            handler.handle(match, player1);

            // Then: no winner
            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }
    }

    @Nested
    @DisplayName("Match Win Tests - Best of 5")
    class BestOfFiveMatchWinTests {

        @BeforeEach
        void setupBestOfFive() {
            matchRules = new BestOfFiveMatchRules();
            handler = new MatchHandler(matchRules);
        }

        @Test
        void handle_3to0_shouldWinMatch() {
            // Given: 3-0 in sets
            awardSets(player1, 3);

            // When
            handler.handle(match, player1);

            // Then: match won
            assertEquals(player1, match.getWinner());
            assertTrue(match.isFinished());
        }

        @Test
        void handle_3to1_shouldWinMatch() {
            // Given: 3-1 in sets
            awardSets(player1, 3);
            awardSets(player2, 1);

            // When
            handler.handle(match, player1);

            // Then: match won
            assertEquals(player1, match.getWinner());
            assertTrue(match.isFinished());
        }

        @Test
        void handle_3to2_shouldWinMatch() {
            // Given: 3-2 in sets
            awardSets(player1, 3);
            awardSets(player2, 2);

            // When
            handler.handle(match, player1);

            // Then: match won
            assertEquals(player1, match.getWinner());
            assertTrue(match.isFinished());
        }

        @Test
        void handle_2to3_player2Wins() {
            // Given: 2-3 in sets
            awardSets(player1, 2);
            awardSets(player2, 3);

            // When
            handler.handle(match, player2);

            // Then: player2 wins
            assertEquals(player2, match.getWinner());
            assertTrue(match.isFinished());
        }
    }

    @Nested
    @DisplayName("Match Not Won Tests - Best of 5")
    class BestOfFiveMatchNotWonTests {

        @BeforeEach
        void setupBestOfFive() {
            matchRules = new BestOfFiveMatchRules();
            handler = new MatchHandler(matchRules);
        }

        @Test
        void handle_2to0_shouldNotWinMatch() {
            // Given: 2-0 in sets (need 3 for best of 5)
            awardSets(player1, 2);

            // When
            handler.handle(match, player1);

            // Then: no winner yet
            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }

        @Test
        void handle_2to1_shouldNotWinMatch() {
            // Given: 2-1 in sets
            awardSets(player1, 2);
            awardSets(player2, 1);

            // When
            handler.handle(match, player1);

            // Then: no winner
            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }

        @Test
        void handle_2to2_shouldNotWinMatch() {
            // Given: 2-2 in sets
            awardSets(player1, 2);
            awardSets(player2, 2);

            // When
            handler.handle(match, player1);

            // Then: no winner (need 3)
            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }

        @Test
        void handle_1to2_shouldNotWinMatch() {
            // Given: 1-2 in sets
            awardSets(player1, 1);
            awardSets(player2, 2);

            // When
            handler.handle(match, player1);

            // Then: no winner
            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }
    }

    @Nested
    @DisplayName("Complete Match Scenarios")
    class CompleteMatchScenarioTests {

        @Test
        void completeMatch_straightSetsWin_2to0() {
            // Simulate player1 winning 2 straight sets
            match.awardSetTo(player1); // Set 1
            handler.handle(match, player1);
            assertFalse(match.isFinished());

            match.awardSetTo(player1); // Set 2
            handler.handle(match, player1);

            // Match won
            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
            assertEquals(2, match.getSets(player1));
            assertEquals(0, match.getSets(player2));
        }

        @Test
        void completeMatch_threeSetBattle_2to1() {
            // Set 1: player1 wins
            match.awardSetTo(player1);
            handler.handle(match, player1);
            assertFalse(match.isFinished());

            // Set 2: player2 wins
            match.awardSetTo(player2);
            handler.handle(match, player2);
            assertFalse(match.isFinished());

            // Set 3: player1 wins
            match.awardSetTo(player1);
            handler.handle(match, player1);

            // Match won
            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
            assertEquals(2, match.getSets(player1));
            assertEquals(1, match.getSets(player2));
        }

        @Test
        void completeMatch_player2Comeback_1to2() {
            // Set 1: player1 wins
            match.awardSetTo(player1);
            handler.handle(match, player1);

            // Set 2: player2 wins
            match.awardSetTo(player2);
            handler.handle(match, player2);

            // Set 3: player2 wins
            match.awardSetTo(player2);
            handler.handle(match, player2);

            // Match won by player2
            assertTrue(match.isFinished());
            assertEquals(player2, match.getWinner());
            assertEquals(1, match.getSets(player1));
            assertEquals(2, match.getSets(player2));
        }
    }

    @Nested
    @DisplayName("Complete Match Scenarios - Best of 5")
    class BestOfFiveCompleteMatchTests {

        @BeforeEach
        void setupBestOfFive() {
            matchRules = new BestOfFiveMatchRules();
            handler = new MatchHandler(matchRules);
        }

        @Test
        void completeMatch_straightSets_3to0() {
            // Player1 wins 3 straight sets
            match.awardSetTo(player1);
            handler.handle(match, player1);
            assertFalse(match.isFinished());

            match.awardSetTo(player1);
            handler.handle(match, player1);
            assertFalse(match.isFinished());

            match.awardSetTo(player1);
            handler.handle(match, player1);

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
        }

        @Test
        void completeMatch_fiveSetter_3to2() {
            // Set 1: player1
            match.awardSetTo(player1);
            handler.handle(match, player1);

            // Set 2: player2
            match.awardSetTo(player2);
            handler.handle(match, player2);

            // Set 3: player1
            match.awardSetTo(player1);
            handler.handle(match, player1);

            // Set 4: player2
            match.awardSetTo(player2);
            handler.handle(match, player2);

            assertEquals(2, match.getSets(player1));
            assertEquals(2, match.getSets(player2));
            assertFalse(match.isFinished());

            // Set 5: player1 wins
            match.awardSetTo(player1);
            handler.handle(match, player1);

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
            assertEquals(3, match.getSets(player1));
            assertEquals(2, match.getSets(player2));
        }

        @Test
        void completeMatch_epicComeback_2to3() {
            // Player2 comes back from 0-2 to win 3-2
            match.awardSetTo(player1); // 1-0
            handler.handle(match, player1);

            match.awardSetTo(player1); // 2-0
            handler.handle(match, player1);

            match.awardSetTo(player2); // 2-1
            handler.handle(match, player2);

            match.awardSetTo(player2); // 2-2
            handler.handle(match, player2);

            assertFalse(match.isFinished());

            match.awardSetTo(player2); // 2-3 - player2 wins
            handler.handle(match, player2);

            assertTrue(match.isFinished());
            assertEquals(player2, match.getWinner());
            assertEquals(2, match.getSets(player1));
            assertEquals(3, match.getSets(player2));
        }
    }

    @Nested
    @DisplayName("Match Already Finished Tests")
    class MatchAlreadyFinishedTests {

        @Test
        void handle_matchAlreadyFinished_shouldNotChangeWinner() {
            // Given: match already finished
            match.setWinner(player1);
            Player initialWinner = match.getWinner();

            // When: try to process again
            handler.handle(match, player2);

            // Then: winner unchanged
            assertEquals(initialWinner, match.getWinner());
            assertEquals(player1, match.getWinner());
        }

        @Test
        void handle_matchFinished_multipleCallsNoEffect() {
            // Given: match finished
            awardSets(player1, 2);
            handler.handle(match, player1);
            assertTrue(match.isFinished());

            // When: multiple calls
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player1);

            // Then: state unchanged
            assertEquals(player1, match.getWinner());
            assertEquals(2, match.getSets(player1));
        }

        @Test
        void handle_afterMatchFinished_stateUnchanged() {
            // Given: match finished with specific state
            awardSets(player1, 2);
            awardSets(player2, 1);
            handler.handle(match, player1);

            int player1Sets = match.getSets(player1);
            int player2Sets = match.getSets(player2);

            // When: try to continue
            handler.handle(match, player2);

            // Then: state unchanged
            assertEquals(player1Sets, match.getSets(player1));
            assertEquals(player2Sets, match.getSets(player2));
            assertEquals(player1, match.getWinner());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        void handle_zeroSets_shouldNotWin() {
            // Given: 0-0

            // When
            handler.handle(match, player1);

            // Then: no winner
            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }

        @Test
        void handle_oneLessThanRequired_shouldNotWin() {
            // Given: 1 set (need 2 for best of 3)
            awardSets(player1, 1);

            // When
            handler.handle(match, player1);

            // Then: no winner
            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }

        @Test
        void handle_exactlyRequired_shouldWin() {
            // Given: exactly 2 sets
            awardSets(player1, 2);

            // When
            handler.handle(match, player1);

            // Then: winner
            assertEquals(player1, match.getWinner());
            assertTrue(match.isFinished());
        }

        @Test
        void handle_moreThanRequired_shouldStillWin() {
            // Given: 3 sets (more than required 2)
            awardSets(player1, 3);

            // When
            handler.handle(match, player1);

            // Then: winner (even though it's impossible in real match)
            assertEquals(player1, match.getWinner());
            assertTrue(match.isFinished());
        }
    }

    @Nested
    @DisplayName("State Preservation Tests")
    class StatePreservationTests {

        @Test
        void handle_matchNotWon_gamesShouldNotReset() {
            // Given: 1-0 in sets, some games in current set
            awardSets(player1, 1);

            for (int i = 0; i < 3; i++) {
                match.awardGameTo(player1);
            }
            for (int i = 0; i < 2; i++) {
                match.awardGameTo(player2);
            }

            // When: match not won yet
            handler.handle(match, player1);

            // Then: games preserved
            assertEquals(3, match.getGames(player1));
            assertEquals(2, match.getGames(player2));
            assertFalse(match.isFinished());
        }

        @Test
        void handle_matchNotWon_pointsShouldNotReset() {
            // Given: 1-1 in sets, some points
            awardSets(player1, 1);
            awardSets(player2, 1);
            match.awardPointTo(player1);
            match.awardPointTo(player1);

            // When
            handler.handle(match, player1);

            // Then: points preserved
            assertEquals(Points.THIRTY, match.getPoints(player1));
            assertFalse(match.isFinished());
        }

        @Test
        void handle_matchWon_setsPreserved() {
            // Given: 2-1, player1 about to win
            awardSets(player1, 2);
            awardSets(player2, 1);

            // When
            handler.handle(match, player1);

            // Then: final score preserved
            assertEquals(2, match.getSets(player1));
            assertEquals(1, match.getSets(player2));
            assertTrue(match.isFinished());
        }
    }

    @Nested
    @DisplayName("Winner Assignment Tests")
    class WinnerAssignmentTests {

        @Test
        void handle_player1Wins_winnerShouldBePlayer1() {
            // Given: player1 reaches required sets
            awardSets(player1, 2);

            // When
            handler.handle(match, player1);

            // Then
            assertEquals(player1, match.getWinner());
            assertNotEquals(player2, match.getWinner());
        }

        @Test
        void handle_player2Wins_winnerShouldBePlayer2() {
            // Given: player2 reaches required sets
            awardSets(player2, 2);

            // When
            handler.handle(match, player2);

            // Then
            assertEquals(player2, match.getWinner());
            assertNotEquals(player1, match.getWinner());
        }

        @Test
        void handle_beforeWin_winnerShouldBeNull() {
            // Given: not enough sets
            awardSets(player1, 1);

            // When
            handler.handle(match, player1);

            // Then
            assertNull(match.getWinner());
        }
    }

    @Nested
    @DisplayName("Different Match Formats")
    class DifferentMatchFormatsTests {

        @Test
        void bestOfThree_requires2Sets() {
            // Best of 3 format
            matchRules = new BestOfThreeMatchRules();
            handler = new MatchHandler(matchRules);

            awardSets(player1, 2);
            handler.handle(match, player1);

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
        }

        @Test
        void bestOfFive_requires3Sets() {
            // Best of 5 format
            matchRules = new BestOfFiveMatchRules();
            handler = new MatchHandler(matchRules);

            // 2 sets not enough
            awardSets(player1, 2);
            handler.handle(match, player1);
            assertFalse(match.isFinished());

            // 3 sets wins
            awardSets(player1, 1); // Now 3 total
            handler.handle(match, player1);
            assertTrue(match.isFinished());
        }

        @Test
        void customFormat_oneSet_shouldWork() {
            // Custom format: first to 1 set
            matchRules = new CustomMatchRules(1);
            handler = new MatchHandler(matchRules);

            awardSets(player1, 1);
            handler.handle(match, player1);

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
        }

        @Test
        void customFormat_fourSets_shouldWork() {
            // Custom format: first to 4 sets
            matchRules = new CustomMatchRules(4);
            handler = new MatchHandler(matchRules);

            awardSets(player1, 4);
            handler.handle(match, player1);

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
        }
    }

    // Helper methods
    private void awardSets(Player player, int sets) {
        for (int i = 0; i < sets; i++) {
            match.awardSetTo(player);
        }
    }

    // Match rules implementations
    private static class BestOfThreeMatchRules implements MatchRules {
        @Override
        public int setsToWinMatch() {
            return 2;
        }
    }

    private static class BestOfFiveMatchRules implements MatchRules {
        @Override
        public int setsToWinMatch() {
            return 3;
        }
    }

    private static class CustomMatchRules implements MatchRules {
        private final int setsToWin;

        public CustomMatchRules(int setsToWin) {
            this.setsToWin = setsToWin;
        }

        @Override
        public int setsToWinMatch() {
            return setsToWin;
        }
    }
}
