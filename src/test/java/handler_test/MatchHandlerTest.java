package handler_test;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.service.scorecalculation.Points;
import com.scoreboard.service.scorecalculation.handler.MatchHandler;
import com.scoreboard.tennisrules.MatchRules;
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
            awardSets(player1, 2);

            handler.handle(match, player1);

            assertEquals(player1, match.getWinner());
            assertTrue(match.isFinished());
        }

        @Test
        void handle_2to1_shouldWinMatch() {
            awardSets(player1, 2);
            awardSets(player2, 1);

            handler.handle(match, player1);

            assertEquals(player1, match.getWinner());
            assertTrue(match.isFinished());
        }

        @Test
        void handle_0to2_player2Wins() {
            awardSets(player2, 2);

            handler.handle(match, player2);

            assertEquals(player2, match.getWinner());
            assertTrue(match.isFinished());
        }

        @Test
        void handle_1to2_player2Wins() {
            awardSets(player1, 1);
            awardSets(player2, 2);

            handler.handle(match, player2);

            assertEquals(player2, match.getWinner());
            assertTrue(match.isFinished());
        }
    }

    @Nested
    @DisplayName("Match Not Won Tests - Best of 3")
    class BestOfThreeMatchNotWonTests {

        @Test
        void handle_0to0_shouldNotWinMatch() {

            handler.handle(match, player1);

            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }

        @Test
        void handle_1to0_shouldNotWinMatch() {
            awardSets(player1, 1);

            handler.handle(match, player1);

            assertNull(match.getWinner());
            assertFalse(match.isFinished());
            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_1to1_shouldNotWinMatch() {
            awardSets(player1, 1);
            awardSets(player2, 1);

            handler.handle(match, player1);

            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }

        @Test
        void handle_0to1_shouldNotWinMatch() {
            awardSets(player2, 1);

            handler.handle(match, player1);

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
            awardSets(player1, 3);

            handler.handle(match, player1);

            assertEquals(player1, match.getWinner());
            assertTrue(match.isFinished());
        }

        @Test
        void handle_3to1_shouldWinMatch() {
            awardSets(player1, 3);
            awardSets(player2, 1);

            handler.handle(match, player1);

            assertEquals(player1, match.getWinner());
            assertTrue(match.isFinished());
        }

        @Test
        void handle_3to2_shouldWinMatch() {
            awardSets(player1, 3);
            awardSets(player2, 2);

            handler.handle(match, player1);

            assertEquals(player1, match.getWinner());
            assertTrue(match.isFinished());
        }

        @Test
        void handle_2to3_player2Wins() {
            awardSets(player1, 2);
            awardSets(player2, 3);

            handler.handle(match, player2);

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
            awardSets(player1, 2);

            handler.handle(match, player1);

            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }

        @Test
        void handle_2to1_shouldNotWinMatch() {
            awardSets(player1, 2);
            awardSets(player2, 1);

            handler.handle(match, player1);

            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }

        @Test
        void handle_2to2_shouldNotWinMatch() {
            awardSets(player1, 2);
            awardSets(player2, 2);

            handler.handle(match, player1);

            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }

        @Test
        void handle_1to2_shouldNotWinMatch() {
            awardSets(player1, 1);
            awardSets(player2, 2);

            handler.handle(match, player1);

            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }
    }

    @Nested
    @DisplayName("Complete Match Scenarios")
    class CompleteMatchScenarioTests {

        @Test
        void completeMatch_straightSetsWin_2to0() {
            match.awardSetTo(player1);
            handler.handle(match, player1);
            assertFalse(match.isFinished());

            match.awardSetTo(player1);
            handler.handle(match, player1);


            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
            assertEquals(2, match.getSets(player1));
            assertEquals(0, match.getSets(player2));
        }

        @Test
        void completeMatch_threeSetBattle_2to1() {
            match.awardSetTo(player1);
            handler.handle(match, player1);
            assertFalse(match.isFinished());

            match.awardSetTo(player2);
            handler.handle(match, player2);
            assertFalse(match.isFinished());

            match.awardSetTo(player1);
            handler.handle(match, player1);

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
            assertEquals(2, match.getSets(player1));
            assertEquals(1, match.getSets(player2));
        }

        @Test
        void completeMatch_player2Comeback_1to2() {
            match.awardSetTo(player1);
            handler.handle(match, player1);

            match.awardSetTo(player2);
            handler.handle(match, player2);

            match.awardSetTo(player2);
            handler.handle(match, player2);

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
            match.awardSetTo(player1);
            handler.handle(match, player1);

            match.awardSetTo(player2);
            handler.handle(match, player2);

            match.awardSetTo(player1);
            handler.handle(match, player1);

            match.awardSetTo(player2);
            handler.handle(match, player2);

            assertEquals(2, match.getSets(player1));
            assertEquals(2, match.getSets(player2));
            assertFalse(match.isFinished());

            match.awardSetTo(player1);
            handler.handle(match, player1);

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
            assertEquals(3, match.getSets(player1));
            assertEquals(2, match.getSets(player2));
        }

        @Test
        void completeMatch_epicComeback_2to3() {
            match.awardSetTo(player1);
            handler.handle(match, player1);

            match.awardSetTo(player1);
            handler.handle(match, player1);

            match.awardSetTo(player2);
            handler.handle(match, player2);

            match.awardSetTo(player2);
            handler.handle(match, player2);

            assertFalse(match.isFinished());

            match.awardSetTo(player2);
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
            match.setWinner(player1);
            Player initialWinner = match.getWinner();

            handler.handle(match, player2);

            assertEquals(initialWinner, match.getWinner());
            assertEquals(player1, match.getWinner());
        }

        @Test
        void handle_matchFinished_multipleCallsNoEffect() {
            awardSets(player1, 2);
            handler.handle(match, player1);
            assertTrue(match.isFinished());

            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player1);

            assertEquals(player1, match.getWinner());
            assertEquals(2, match.getSets(player1));
        }

        @Test
        void handle_afterMatchFinished_stateUnchanged() {
            awardSets(player1, 2);
            awardSets(player2, 1);
            handler.handle(match, player1);

            int player1Sets = match.getSets(player1);
            int player2Sets = match.getSets(player2);

            handler.handle(match, player2);

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
            handler.handle(match, player1);

            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }

        @Test
        void handle_oneLessThanRequired_shouldNotWin() {
            awardSets(player1, 1);

            handler.handle(match, player1);

            assertNull(match.getWinner());
            assertFalse(match.isFinished());
        }

        @Test
        void handle_exactlyRequired_shouldWin() {
            awardSets(player1, 2);

            handler.handle(match, player1);

            assertEquals(player1, match.getWinner());
            assertTrue(match.isFinished());
        }

        @Test
        void handle_moreThanRequired_shouldStillWin() {
            awardSets(player1, 3);

            handler.handle(match, player1);

            assertEquals(player1, match.getWinner());
            assertTrue(match.isFinished());
        }
    }

    @Nested
    @DisplayName("State Preservation Tests")
    class StatePreservationTests {

        @Test
        void handle_matchNotWon_gamesShouldNotReset() {
            awardSets(player1, 1);

            for (int i = 0; i < 3; i++) {
                match.awardGameTo(player1);
            }
            for (int i = 0; i < 2; i++) {
                match.awardGameTo(player2);
            }

            handler.handle(match, player1);

            assertEquals(3, match.getGames(player1));
            assertEquals(2, match.getGames(player2));
            assertFalse(match.isFinished());
        }

        @Test
        void handle_matchNotWon_pointsShouldNotReset() {
            awardSets(player1, 1);
            awardSets(player2, 1);
            match.awardPointTo(player1);
            match.awardPointTo(player1);

            handler.handle(match, player1);

            assertEquals(Points.THIRTY, match.getPoints(player1));
            assertFalse(match.isFinished());
        }

        @Test
        void handle_matchWon_setsPreserved() {
            awardSets(player1, 2);
            awardSets(player2, 1);

            handler.handle(match, player1);

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
            awardSets(player1, 2);

            handler.handle(match, player1);

            assertEquals(player1, match.getWinner());
            assertNotEquals(player2, match.getWinner());
        }

        @Test
        void handle_player2Wins_winnerShouldBePlayer2() {
            awardSets(player2, 2);

            handler.handle(match, player2);

            assertEquals(player2, match.getWinner());
            assertNotEquals(player1, match.getWinner());
        }

        @Test
        void handle_beforeWin_winnerShouldBeNull() {
            awardSets(player1, 1);

            handler.handle(match, player1);

            assertNull(match.getWinner());
        }
    }

    @Nested
    @DisplayName("Different Match Formats")
    class DifferentMatchFormatsTests {

        @Test
        void bestOfThree_requires2Sets() {
            matchRules = new BestOfThreeMatchRules();
            handler = new MatchHandler(matchRules);

            awardSets(player1, 2);
            handler.handle(match, player1);

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
        }

        @Test
        void bestOfFive_requires3Sets() {
            matchRules = new BestOfFiveMatchRules();
            handler = new MatchHandler(matchRules);

            awardSets(player1, 2);
            handler.handle(match, player1);
            assertFalse(match.isFinished());

            awardSets(player1, 1);
            handler.handle(match, player1);
            assertTrue(match.isFinished());
        }

        @Test
        void customFormat_oneSet_shouldWork() {
            matchRules = new CustomMatchRules(1);
            handler = new MatchHandler(matchRules);

            awardSets(player1, 1);
            handler.handle(match, player1);

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
        }

        @Test
        void customFormat_fourSets_shouldWork() {
            matchRules = new CustomMatchRules(4);
            handler = new MatchHandler(matchRules);

            awardSets(player1, 4);
            handler.handle(match, player1);

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
        }
    }

    private void awardSets(Player player, int sets) {
        for (int i = 0; i < sets; i++) {
            match.awardSetTo(player);
        }
    }

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
