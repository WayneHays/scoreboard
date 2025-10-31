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
            // Given: player1 has 6 games, player2 has 0
            awardGames(player1, 6);

            // When: handler processes
            handler.handle(match, player1);

            // Then: set won (6 games, difference >= 2)
            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getSets(player2));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
        }

        @Test
        void handle_6to1_shouldWinSet() {
            // Given: 6-1
            awardGames(player1, 6);
            awardGames(player2, 1);

            // When
            handler.handle(match, player1);

            // Then
            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getSets(player2));
        }

        @Test
        void handle_6to2_shouldWinSet() {
            // Given: 6-2
            awardGames(player1, 6);
            awardGames(player2, 2);

            // When
            handler.handle(match, player1);

            // Then
            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_6to3_shouldWinSet() {
            // Given: 6-3
            awardGames(player1, 6);
            awardGames(player2, 3);

            // When
            handler.handle(match, player1);

            // Then
            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_6to4_shouldWinSet() {
            // Given: 6-4
            awardGames(player1, 6);
            awardGames(player2, 4);

            // When
            handler.handle(match, player1);

            // Then
            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_player2Wins6to2() {
            // Given: 2-6
            awardGames(player1, 2);
            awardGames(player2, 6);

            // When
            handler.handle(match, player2);

            // Then
            assertEquals(0, match.getSets(player1));
            assertEquals(1, match.getSets(player2));
        }
    }

    @Nested
    @DisplayName("Set Win Tests - Extended Play")
    class ExtendedSetWinTests {

        @Test
        void handle_7to5_shouldWinSet() {
            // Given: 7-5
            awardGames(player1, 7);
            awardGames(player2, 5);

            // When
            handler.handle(match, player1);

            // Then: set won (7 games, difference = 2)
            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_8to6_shouldWinSet() {
            // Given: 8-6 (extended play without tiebreak)
            awardGames(player1, 8);
            awardGames(player2, 6);

            // When
            handler.handle(match, player1);

            // Then
            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_9to7_shouldWinSet() {
            // Given: 9-7
            awardGames(player1, 9);
            awardGames(player2, 7);

            // When
            handler.handle(match, player1);

            // Then
            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_10to8_shouldWinSet() {
            // Given: 10-8
            awardGames(player1, 10);
            awardGames(player2, 8);

            // When
            handler.handle(match, player1);

            // Then
            assertEquals(1, match.getSets(player1));
        }
    }

    @Nested
    @DisplayName("Set Not Won - Insufficient Lead")
    class SetNotWonTests {

        @Test
        void handle_6to5_shouldNotWinSet() {
            // Given: 6-5 (difference = 1, need 2)
            awardGames(player1, 6);
            awardGames(player2, 5);

            // When
            handler.handle(match, player1);

            // Then: set not won
            assertEquals(0, match.getSets(player1));
            assertEquals(0, match.getSets(player2));
            assertEquals(6, match.getGames(player1));
            assertEquals(5, match.getGames(player2));
        }

        @Test
        void handle_5to4_shouldNotWinSet() {
            // Given: 5-4
            awardGames(player1, 5);
            awardGames(player2, 4);

            // When
            handler.handle(match, player1);

            // Then: set not won (need 6 games minimum)
            assertEquals(0, match.getSets(player1));
            assertEquals(5, match.getGames(player1));
        }

        @Test
        void handle_5to3_shouldNotWinSet() {
            // Given: 5-3
            awardGames(player1, 5);
            awardGames(player2, 3);

            // When
            handler.handle(match, player1);

            // Then: set not won
            assertEquals(0, match.getSets(player1));
        }
    }

    @Nested
    @DisplayName("Tiebreak Activation Tests")
    class TiebreakActivationTests {

        @Test
        void handle_6to6_shouldActivateTiebreak() {
            // Given: 6-6
            awardGames(player1, 6);
            awardGames(player2, 6);

            // When
            handler.handle(match, player1);

            // Then: tiebreak activated
            assertTrue(match.isTieBreak());
            assertEquals(0, match.getSets(player1));
            assertEquals(0, match.getSets(player2));
        }

        @Test
        void handle_6to6_tiebreakEnabled_shouldActivate() {
            // Given: 6-6, tiebreak enabled in rules
            awardGames(player1, 6);
            awardGames(player2, 6);

            // When
            handler.handle(match, player1);

            // Then
            assertTrue(match.isTieBreak());
            assertEquals(6, match.getGames(player1));
            assertEquals(6, match.getGames(player2));
        }

        @Test
        void handle_beforeSixSix_shouldNotActivateTiebreak() {
            // Given: 5-5
            awardGames(player1, 5);
            awardGames(player2, 5);

            // When
            handler.handle(match, player1);

            // Then: no tiebreak
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_6to5_shouldNotActivateTiebreak() {
            // Given: 6-5
            awardGames(player1, 6);
            awardGames(player2, 5);

            // When
            handler.handle(match, player1);

            // Then: no tiebreak
            assertFalse(match.isTieBreak());
        }
    }

    @Nested
    @DisplayName("Tiebreak Win Scenario")
    class TiebreakWinScenarioTests {

        @Test
        void handle_7to6_afterTiebreak_shouldWinSet() {
            // Given: 7-6 (this score appears after tiebreak win)
            awardGames(player1, 7);
            awardGames(player2, 6);

            // When
            handler.handle(match, player1);

            // Then: set won (special case: 7-6 after tiebreak)
            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getSets(player2));
        }

        @Test
        void handle_6to7_afterTiebreak_player2Wins() {
            // Given: 6-7 (player2 won tiebreak)
            awardGames(player1, 6);
            awardGames(player2, 7);

            // When
            handler.handle(match, player2);

            // Then: set won by player2
            assertEquals(0, match.getSets(player1));
            assertEquals(1, match.getSets(player2));
        }
    }

    @Nested
    @DisplayName("State Reset After Set Win")
    class StateResetTests {

        @Test
        void handle_afterSetWin_shouldResetGames() {
            // Given: 6-3
            awardGames(player1, 6);
            awardGames(player2, 3);

            // When: set won
            handler.handle(match, player1);

            // Then: games reset
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
        }

        @Test
        void handle_afterSetWin_shouldResetPoints() {
            // Given: 6-2, with some points
            awardGames(player1, 6);
            awardGames(player2, 2);
            match.awardPointTo(player1);
            match.awardPointTo(player1);

            // When: set won
            handler.handle(match, player1);

            // Then: points reset
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void handle_afterSetWin_shouldDisableTiebreak() {
            // Given: 6-4, but tiebreak was active for some reason
            awardGames(player1, 6);
            awardGames(player2, 4);
            match.setTieBreak(true);

            // When: set won
            handler.handle(match, player1);

            // Then: tiebreak disabled
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_multipleSets_gamesResetEachTime() {
            // Set 1: player1 wins 6-2
            awardGames(player1, 6);
            awardGames(player2, 2);
            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));

            // Set 2: player2 wins 6-3
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
            // Set 1: player1 wins
            awardGames(player1, 6);
            awardGames(player2, 3);
            handler.handle(match, player1);

            // Set 2: player2 wins
            awardGames(player2, 6);
            awardGames(player1, 4);
            handler.handle(match, player2);

            // Set 3: player1 wins
            awardGames(player1, 6);
            awardGames(player2, 2);
            handler.handle(match, player1);

            // Then: 2-1 in sets
            assertEquals(2, match.getSets(player1));
            assertEquals(1, match.getSets(player2));
        }

        @Test
        void handle_alternatingSets_shouldTrackCorrectly() {
            // Set 1: player1 (6-4)
            awardGames(player1, 6);
            awardGames(player2, 4);
            handler.handle(match, player1);
            assertEquals(1, match.getSets(player1));

            // Set 2: player2 (6-3)
            awardGames(player2, 6);
            awardGames(player1, 3);
            handler.handle(match, player2);
            assertEquals(1, match.getSets(player2));

            // Set 3: player1 (7-5)
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
            // Given: match is finished
            match.setWinner(player1);
            awardGames(player1, 6);
            awardGames(player2, 3);
            int initialSets = match.getSets(player1);

            // When: try to award set
            handler.handle(match, player1);

            // Then: no set awarded
            assertEquals(initialSets, match.getSets(player1));
            assertEquals(6, match.getGames(player1)); // Games not reset
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        void handle_lessThanSixGames_shouldNotWinSet() {
            // Given: 5-0
            awardGames(player1, 5);

            // When
            handler.handle(match, player1);

            // Then: no set won
            assertEquals(0, match.getSets(player1));
        }

        @Test
        void handle_exactlySixWithInsufficientLead_shouldNotWin() {
            // Given: 6-5
            awardGames(player1, 6);
            awardGames(player2, 5);

            // When
            handler.handle(match, player1);

            // Then: no set won
            assertEquals(0, match.getSets(player1));
        }

        @Test
        void handle_highScoreWithTwoGameLead_shouldWin() {
            // Given: 12-10
            awardGames(player1, 12);
            awardGames(player2, 10);

            // When
            handler.handle(match, player1);

            // Then: set won
            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_zeroZero_shouldNotWinOrActivateTiebreak() {
            // Given: 0-0

            // When
            handler.handle(match, player1);

            // Then: nothing happens
            assertEquals(0, match.getSets(player1));
            assertFalse(match.isTieBreak());
        }
    }

    @Nested
    @DisplayName("Complete Set Scenarios")
    class CompleteSetScenarioTests {

        @Test
        void completeSet_straightWin_6to0() {
            // Simulate winning 6 straight games
            for (int i = 0; i < 6; i++) {
                match.awardGameTo(player1);
            }

            // Process set
            handler.handle(match, player1);

            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getGames(player1));
        }

        @Test
        void completeSet_close_7to5() {
            // Simulate 7-5 set
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
            // Simulate 6-6, then tiebreak, then 7-6
            for (int i = 0; i < 6; i++) {
                match.awardGameTo(player1);
                match.awardGameTo(player2);
            }

            // Check tiebreak activation at 6-6
            handler.handle(match, player1);
            assertTrue(match.isTieBreak());

            // Simulate tiebreak win (this would be done by TiebreakHandler)
            match.awardGameTo(player1); // Now 7-6
            match.setTieBreak(false);

            // Process set win
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
            // Given: 6-6, but tiebreak disabled
            awardGames(player1, 6);
            awardGames(player2, 6);

            // When
            handler.handle(match, player1);

            // Then: no tiebreak activated
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_7to5_tiebreakDisabled_shouldWinSet() {
            // Given: 7-5
            awardGames(player1, 7);
            awardGames(player2, 5);

            // When
            handler.handle(match, player1);

            // Then: set won normally
            assertEquals(1, match.getSets(player1));
        }

        @Test
        void handle_8to6_tiebreakDisabled_shouldWinSet() {
            // Given: 8-6 (would happen without tiebreak)
            awardGames(player1, 8);
            awardGames(player2, 6);

            // When
            handler.handle(match, player1);

            // Then: set won
            assertEquals(1, match.getSets(player1));
        }
    }

    @Nested
    @DisplayName("Integration with Game State")
    class IntegrationTests {

        @Test
        void handle_setWonWithPartialGame_shouldResetPointsAndGames() {
            // Given: 6-2, player1 has some points in current game
            awardGames(player1, 6);
            awardGames(player2, 2);
            match.awardPointTo(player1); // 15-0
            match.awardPointTo(player1); // 30-0

            // When: set won
            handler.handle(match, player1);

            // Then: points and games reset
            assertEquals(1, match.getSets(player1));
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertFalse(match.isTieBreak());
        }

        @Test
        void handle_setWonAfterTiebreak_shouldResetEverything() {
            // Given: 6-6, tiebreak activated, then player1 wins tiebreak (7-6)
            awardGames(player1, 6);
            awardGames(player2, 6);

            // Activate tiebreak at 6-6
            handler.handle(match, player1);
            assertTrue(match.isTieBreak());

            // Simulate tiebreak win (done by TiebreakHandler normally)
            match.awardGameTo(player1); // 7-6
            match.setTieBreak(false);

            // When: process set win
            handler.handle(match, player1);

            // Then: everything reset
            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertFalse(match.isTieBreak());
        }
    }

    // Helper methods
    private void awardGames(Player player, int games) {
        for (int i = 0; i < games; i++) {
            match.awardGameTo(player);
        }
    }

    // Standard set rules implementation
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

    // No tiebreak set rules implementation
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
