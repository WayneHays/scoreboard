package handler_test;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.service.scorecalculation.Points;
import com.scoreboard.service.scorecalculation.handler.SetHandler;
import com.scoreboard.service.scorecalculation.rules.SetRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SetHandlerTest {
    private Player player1;
    private Player player2;
    private OngoingMatch match;
    private SetHandler handler;
    private SetRules setRules;

    @BeforeEach
    void setUp() {
        player1 = new Player("Novak Djokovic");
        player2 = new Player("Rafael Nadal");
        match = new OngoingMatch(player1, player2);
        setRules = new StandardSetRules();
        handler = new SetHandler(setRules);
    }

    @Nested
    @DisplayName("Set Win Tests - Standard Scenarios")
    class StandardSetWinTests {

        @Test
        void handle_6to0_shouldWinSet() {
            awardGames(player1, 6);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getSets(player2));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
        }

        @Test
        void handle_6to1_shouldWinSet() {
            awardGames(player1, 6);
            awardGames(player2, 1);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getSets(player2));
        }

        @Test
        void handle_6to2_shouldWinSet() {
            awardGames(player1, 6);
            awardGames(player2, 2);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_6to3_shouldWinSet() {
            awardGames(player1, 6);
            awardGames(player2, 3);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_6to4_shouldWinSet() {
            awardGames(player1, 6);
            awardGames(player2, 4);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_player2Wins6to2() {
            awardGames(player1, 2);
            awardGames(player2, 6);

            handler.handle(match, player2);

            assertEquals(0, match.getSets(player1));
            assertEquals(1, match.getSets(player2));
        }
    }

    @Nested
    @DisplayName("Set Win Tests - Extended Play")
    class ExtendedSetWinTests {

        @Test
        void handle_7to5_shouldWinSet() {
            awardGames(player1, 7);
            awardGames(player2, 5);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_8to6_shouldWinSet() {
            awardGames(player1, 8);
            awardGames(player2, 6);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_9to7_shouldWinSet() {
            awardGames(player1, 9);
            awardGames(player2, 7);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_10to8_shouldWinSet() {
            awardGames(player1, 10);
            awardGames(player2, 8);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
        }
    }

    @Nested
    @DisplayName("Set Not Won - Insufficient Lead")
    class SetNotWonTests {

        @Test
        void handle_6to5_shouldNotWinSet() {
            awardGames(player1, 6);
            awardGames(player2, 5);

            handler.handle(match, player1);

            assertEquals(0, match.getSets(player1));
            assertEquals(0, match.getSets(player2));
            assertEquals(6, match.getGames(player1));
            assertEquals(5, match.getGames(player2));
        }

        @Test
        void handle_5to4_shouldNotWinSet() {
            awardGames(player1, 5);
            awardGames(player2, 4);

            handler.handle(match, player1);

            assertEquals(0, match.getSets(player1));
            assertEquals(5, match.getGames(player1));
        }

        @Test
        void handle_5to3_shouldNotWinSet() {
            awardGames(player1, 5);
            awardGames(player2, 3);

            handler.handle(match, player1);

            assertEquals(0, match.getSets(player1));
        }
    }

    @Nested
    @DisplayName("Tiebreak Activation Tests")
    class TiebreakActivationTests {

        @Test
        void handle_6to6_shouldActivateTiebreak() {
            awardGames(player1, 6);
            awardGames(player2, 6);

            handler.handle(match, player1);

            assertTrue(match.isTieBreak());
            assertEquals(0, match.getSets(player1));
            assertEquals(0, match.getSets(player2));
        }

        @Test
        void handle_6to6_tiebreakEnabled_shouldActivate() {
            awardGames(player1, 6);
            awardGames(player2, 6);

            handler.handle(match, player1);

            assertTrue(match.isTieBreak());
            assertEquals(6, match.getGames(player1));
            assertEquals(6, match.getGames(player2));
        }

        @Test
        void handle_beforeSixSix_shouldNotActivateTiebreak() {
            awardGames(player1, 5);
            awardGames(player2, 5);

            handler.handle(match, player1);

            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_6to5_shouldNotActivateTiebreak() {
            awardGames(player1, 6);
            awardGames(player2, 5);

            handler.handle(match, player1);

            assertFalse(match.isTieBreak());
        }
    }

    @Nested
    @DisplayName("Tiebreak Win Scenario")
    class TiebreakWinScenarioTests {

        @Test
        void handle_7to6_afterTiebreak_shouldWinSet() {
            awardGames(player1, 7);
            awardGames(player2, 6);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getSets(player2));
        }

        @Test
        void handle_6to7_afterTiebreak_player2Wins() {
            awardGames(player1, 6);
            awardGames(player2, 7);

            handler.handle(match, player2);

            assertEquals(0, match.getSets(player1));
            assertEquals(1, match.getSets(player2));
        }
    }

    @Nested
    @DisplayName("State Reset After Set Win")
    class StateResetTests {

        @Test
        void handle_afterSetWin_shouldResetGames() {
            awardGames(player1, 6);
            awardGames(player2, 3);

            handler.handle(match, player1);

            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
        }

        @Test
        void handle_afterSetWin_shouldResetPoints() {
            awardGames(player1, 6);
            awardGames(player2, 2);
            match.awardPointTo(player1);
            match.awardPointTo(player1);

            handler.handle(match, player1);

            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void handle_afterSetWin_shouldDisableTiebreak() {
            awardGames(player1, 6);
            awardGames(player2, 4);
            match.setTieBreak(true);

            handler.handle(match, player1);

            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_multipleSets_gamesResetEachTime() {
            awardGames(player1, 6);
            awardGames(player2, 2);
            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));

            awardGames(player2, 6);
            awardGames(player1, 3);
            handler.handle(match, player2);

            assertEquals(1, match.getSets(player1));
            assertEquals(1, match.getSets(player2));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
        }
    }

    @Nested
    @DisplayName("Multiple Sets Scenarios")
    class MultipleSetsTests {

        @Test
        void handle_threeSets_shouldAccumulateSets() {
            awardGames(player1, 6);
            awardGames(player2, 3);
            handler.handle(match, player1);

            awardGames(player2, 6);
            awardGames(player1, 4);
            handler.handle(match, player2);

            awardGames(player1, 6);
            awardGames(player2, 2);
            handler.handle(match, player1);

            assertEquals(2, match.getSets(player1));
            assertEquals(1, match.getSets(player2));
        }

        @Test
        void handle_alternatingSets_shouldTrackCorrectly() {
            awardGames(player1, 6);
            awardGames(player2, 4);
            handler.handle(match, player1);
            assertEquals(1, match.getSets(player1));

            awardGames(player2, 6);
            awardGames(player1, 3);
            handler.handle(match, player2);
            assertEquals(1, match.getSets(player2));

            awardGames(player1, 7);
            awardGames(player2, 5);
            handler.handle(match, player1);
            assertEquals(2, match.getSets(player1));
        }
    }

    @Nested
    @DisplayName("Match Finished Tests")
    class MatchFinishedTests {

        @Test
        void handle_matchAlreadyFinished_shouldNotProcessSet() {
            match.setWinner(player1);
            awardGames(player1, 6);
            awardGames(player2, 3);
            int initialSets = match.getSets(player1);

            handler.handle(match, player1);

            assertEquals(initialSets, match.getSets(player1));
            assertEquals(6, match.getGames(player1)); // Games not reset
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        void handle_lessThanSixGames_shouldNotWinSet() {
            awardGames(player1, 5);

            handler.handle(match, player1);

            assertEquals(0, match.getSets(player1));
        }

        @Test
        void handle_exactlySixWithInsufficientLead_shouldNotWin() {
            awardGames(player1, 6);
            awardGames(player2, 5);

            handler.handle(match, player1);

            assertEquals(0, match.getSets(player1));
        }

        @Test
        void handle_highScoreWithTwoGameLead_shouldWin() {
            awardGames(player1, 12);
            awardGames(player2, 10);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_zeroZero_shouldNotWinOrActivateTiebreak() {
            handler.handle(match, player1);

            assertEquals(0, match.getSets(player1));
            assertFalse(match.isTieBreak());
        }
    }

    @Nested
    @DisplayName("Complete Set Scenarios")
    class CompleteSetScenarioTests {

        @Test
        void completeSet_straightWin_6to0() {
            for (int i = 0; i < 6; i++) {
                match.awardGameTo(player1);
            }

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getGames(player1));
        }

        @Test
        void completeSet_close_7to5() {
            for (int i = 0; i < 7; i++) {
                match.awardGameTo(player1);
            }
            for (int i = 0; i < 5; i++) {
                match.awardGameTo(player2);
            }

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
        }

        @Test
        void completeSet_withTiebreak_7to6() {
            for (int i = 0; i < 6; i++) {
                match.awardGameTo(player1);
                match.awardGameTo(player2);
            }

            handler.handle(match, player1);
            assertTrue(match.isTieBreak());

            match.awardGameTo(player1);
            match.setTieBreak(false);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
        }
    }

    @Nested
    @DisplayName("Tiebreak Rules Disabled Scenario")
    class NoTiebreakRulesTests {

        @BeforeEach
        void setupNoTiebreakRules() {
            setRules = new NoTiebreakSetRules();
            handler = new SetHandler(setRules);
        }

        @Test
        void handle_6to6_tiebreakDisabled_shouldNotActivateTiebreak() {
            awardGames(player1, 6);
            awardGames(player2, 6);

            handler.handle(match, player1);

            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_7to5_tiebreakDisabled_shouldWinSet() {
            awardGames(player1, 7);
            awardGames(player2, 5);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_8to6_tiebreakDisabled_shouldWinSet() {
            awardGames(player1, 8);
            awardGames(player2, 6);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
        }
    }

    @Nested
    @DisplayName("Integration with Game State")
    class IntegrationTests {

        @Test
        void handle_setWonWithPartialGame_shouldResetPointsAndGames() {
            awardGames(player1, 6);
            awardGames(player2, 2);
            match.awardPointTo(player1);
            match.awardPointTo(player1);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_setWonAfterTiebreak_shouldResetEverything() {
            awardGames(player1, 6);
            awardGames(player2, 6);

            handler.handle(match, player1);
            assertTrue(match.isTieBreak());

            match.awardGameTo(player1);
            match.setTieBreak(false);

            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertFalse(match.isTieBreak());
        }
    }

    private void awardGames(Player player, int games) {
        for (int i = 0; i < games; i++) {
            match.awardGameTo(player);
        }
    }

    private static class StandardSetRules implements SetRules {
        @Override
        public int gamesToWinSet() {
            return 6;
        }

        @Override
        public int minGamesDifferenceToWinSet() {
            return 2;
        }

        @Override
        public boolean isTiebreakEnabled() {
            return true;
        }
    }

    private static class NoTiebreakSetRules implements SetRules {
        @Override
        public int gamesToWinSet() {
            return 6;
        }

        @Override
        public int minGamesDifferenceToWinSet() {
            return 2;
        }

        @Override
        public boolean isTiebreakEnabled() {
            return false;
        }
    }
}
