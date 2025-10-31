import com.scoreboard.model.entity.Player;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.service.scorecalculation.Points;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OngoingMatchTest {

    private Player player1;
    private Player player2;
    private OngoingMatch ongoingMatch;

    @BeforeEach
    void setUp() {
        player1 = new Player("Roger Federer");
        player2 = new Player("Rafael Nadal");
        ongoingMatch = new OngoingMatch(player1, player2);
    }

    @Nested
    @DisplayName("Constructor and Initial State Tests")
    class ConstructorTests {

        @Test
        void constructor_shouldInitializePlayers() {
            assertEquals(player1, ongoingMatch.getPlayer1());
            assertEquals(player2, ongoingMatch.getPlayer2());
        }

        @Test
        void constructor_shouldInitializeScoresToZero() {
            assertEquals(Points.ZERO, ongoingMatch.getPoints(player1));
            assertEquals(Points.ZERO, ongoingMatch.getPoints(player2));
            assertEquals(0, ongoingMatch.getGames(player1));
            assertEquals(0, ongoingMatch.getGames(player2));
            assertEquals(0, ongoingMatch.getSets(player1));
            assertEquals(0, ongoingMatch.getSets(player2));
            assertEquals(0, ongoingMatch.getTieBreakPoints(player1));
            assertEquals(0, ongoingMatch.getTieBreakPoints(player2));
        }

        @Test
        void constructor_shouldInitializeWinnerAsNull() {
            assertNull(ongoingMatch.getWinner());
        }

        @Test
        void constructor_shouldInitializeAdvantageAsNull() {
            assertNull(ongoingMatch.getAdvantage());
        }

        @Test
        void constructor_shouldInitializeTieBreakAsFalse() {
            assertFalse(ongoingMatch.isTieBreak());
        }

        @Test
        void isFinished_initialState_shouldReturnFalse() {
            assertFalse(ongoingMatch.isFinished());
        }
    }

    @Nested
    @DisplayName("Player Lookup Tests")
    class PlayerLookupTests {

        @Test
        void getPlayerByName_player1Name_shouldReturnPlayer1() {
            Player result = ongoingMatch.getPlayerByName("Roger Federer");
            assertEquals(player1, result);
        }

        @Test
        void getPlayerByName_player2Name_shouldReturnPlayer2() {
            Player result = ongoingMatch.getPlayerByName("Rafael Nadal");
            assertEquals(player2, result);
        }

        @Test
        void getPlayerByName_unknownName_shouldReturnPlayer2() {
            // Текущая логика возвращает player2 если имя не совпадает с player1
            Player result = ongoingMatch.getPlayerByName("Unknown Player");
            assertEquals(player2, result);
        }

        @Test
        void getOpponent_player1_shouldReturnPlayer2() {
            assertEquals(player2, ongoingMatch.getOpponent(player1));
        }

        @Test
        void getOpponent_player2_shouldReturnPlayer1() {
            assertEquals(player1, ongoingMatch.getOpponent(player2));
        }
    }

    @Nested
    @DisplayName("Point Award Tests")
    class PointAwardTests {

        @Test
        void awardPointTo_player1_shouldIncrementPoints() {
            ongoingMatch.awardPointTo(player1);
            assertEquals(Points.FIFTEEN, ongoingMatch.getPoints(player1));
            assertEquals(Points.ZERO, ongoingMatch.getPoints(player2));
        }

        @Test
        void awardPointTo_multiplePoints_shouldProgressCorrectly() {
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardPointTo(player1);

            assertEquals(Points.FORTY, ongoingMatch.getPoints(player1));
            assertEquals(Points.ZERO, ongoingMatch.getPoints(player2));
        }

        @Test
        void awardPointTo_bothPlayers_shouldTrackSeparately() {
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardPointTo(player2);
            ongoingMatch.awardPointTo(player1);

            assertEquals(Points.THIRTY, ongoingMatch.getPoints(player1));
            assertEquals(Points.FIFTEEN, ongoingMatch.getPoints(player2));
        }

        @Test
        void awardPointTo_toAdvantage_shouldWork() {
            // Setup: 40-40, then player1 scores
            ongoingMatch.awardPointTo(player1); // 15-0
            ongoingMatch.awardPointTo(player1); // 30-0
            ongoingMatch.awardPointTo(player1); // 40-0
            ongoingMatch.awardPointTo(player2); // 40-15
            ongoingMatch.awardPointTo(player2); // 40-30
            ongoingMatch.awardPointTo(player2); // 40-40
            ongoingMatch.awardPointTo(player1); // AD-40

            assertEquals(Points.ADVANTAGE, ongoingMatch.getPoints(player1));
            assertEquals(Points.FORTY, ongoingMatch.getPoints(player2));
        }
    }

    @Nested
    @DisplayName("Game Award Tests")
    class GameAwardTests {

        @Test
        void awardGameTo_player1_shouldIncrementGames() {
            ongoingMatch.awardGameTo(player1);

            assertEquals(1, ongoingMatch.getGames(player1));
            assertEquals(0, ongoingMatch.getGames(player2));
        }

        @Test
        void awardGameTo_shouldResetPoints() {
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardPointTo(player2);

            ongoingMatch.awardGameTo(player1);

            assertEquals(Points.ZERO, ongoingMatch.getPoints(player1));
            assertEquals(Points.ZERO, ongoingMatch.getPoints(player2));
        }

        @Test
        void awardGameTo_shouldResetTieBreakPoints() {
            ongoingMatch.setTieBreak(true);
            ongoingMatch.awardTieBreakPointTo(player1);
            ongoingMatch.awardTieBreakPointTo(player1);
            ongoingMatch.awardTieBreakPointTo(player2);

            ongoingMatch.awardGameTo(player1);

            assertEquals(0, ongoingMatch.getTieBreakPoints(player1));
            assertEquals(0, ongoingMatch.getTieBreakPoints(player2));
        }

        @Test
        void awardGameTo_shouldResetAdvantage() {
            ongoingMatch.setAdvantage(player1);

            ongoingMatch.awardGameTo(player1);

            assertNull(ongoingMatch.getAdvantage());
        }

        @Test
        void awardGameTo_multipleGames_shouldAccumulate() {
            ongoingMatch.awardGameTo(player1);
            ongoingMatch.awardGameTo(player1);
            ongoingMatch.awardGameTo(player2);
            ongoingMatch.awardGameTo(player1);

            assertEquals(3, ongoingMatch.getGames(player1));
            assertEquals(1, ongoingMatch.getGames(player2));
        }
    }

    @Nested
    @DisplayName("Set Award Tests")
    class SetAwardTests {

        @Test
        void awardSetTo_player1_shouldIncrementSets() {
            ongoingMatch.awardSetTo(player1);

            assertEquals(1, ongoingMatch.getSets(player1));
            assertEquals(0, ongoingMatch.getSets(player2));
        }

        @Test
        void awardSetTo_shouldResetGames() {
            ongoingMatch.awardGameTo(player1);
            ongoingMatch.awardGameTo(player1);
            ongoingMatch.awardGameTo(player2);

            ongoingMatch.awardSetTo(player1);

            assertEquals(0, ongoingMatch.getGames(player1));
            assertEquals(0, ongoingMatch.getGames(player2));
        }

        @Test
        void awardSetTo_shouldResetPoints() {
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardPointTo(player2);

            ongoingMatch.awardSetTo(player1);

            assertEquals(Points.ZERO, ongoingMatch.getPoints(player1));
            assertEquals(Points.ZERO, ongoingMatch.getPoints(player2));
        }

        @Test
        void awardSetTo_shouldDisableTieBreak() {
            ongoingMatch.setTieBreak(true);

            ongoingMatch.awardSetTo(player1);

            assertFalse(ongoingMatch.isTieBreak());
        }

        @Test
        void awardSetTo_multipleSets_shouldAccumulate() {
            ongoingMatch.awardSetTo(player1);
            ongoingMatch.awardSetTo(player2);
            ongoingMatch.awardSetTo(player1);

            assertEquals(2, ongoingMatch.getSets(player1));
            assertEquals(1, ongoingMatch.getSets(player2));
        }
    }

    @Nested
    @DisplayName("Tiebreak Tests")
    class TiebreakTests {

        @Test
        void awardTieBreakPointTo_player1_shouldIncrementTieBreakPoints() {
            ongoingMatch.awardTieBreakPointTo(player1);

            assertEquals(1, ongoingMatch.getTieBreakPoints(player1));
            assertEquals(0, ongoingMatch.getTieBreakPoints(player2));
        }

        @Test
        void awardTieBreakPointTo_multiplePoints_shouldAccumulate() {
            ongoingMatch.awardTieBreakPointTo(player1);
            ongoingMatch.awardTieBreakPointTo(player1);
            ongoingMatch.awardTieBreakPointTo(player2);
            ongoingMatch.awardTieBreakPointTo(player1);

            assertEquals(3, ongoingMatch.getTieBreakPoints(player1));
            assertEquals(1, ongoingMatch.getTieBreakPoints(player2));
        }

        @Test
        void setTieBreak_true_shouldEnableTieBreak() {
            ongoingMatch.setTieBreak(true);
            assertTrue(ongoingMatch.isTieBreak());
        }

        @Test
        void setTieBreak_false_shouldDisableTieBreak() {
            ongoingMatch.setTieBreak(true);
            ongoingMatch.setTieBreak(false);
            assertFalse(ongoingMatch.isTieBreak());
        }
    }

    @Nested
    @DisplayName("Advantage Tests")
    class AdvantageTests {

        @Test
        void setAdvantage_player1_shouldSetAdvantage() {
            ongoingMatch.setAdvantage(player1);
            assertEquals(player1, ongoingMatch.getAdvantage());
        }

        @Test
        void setAdvantage_null_shouldClearAdvantage() {
            ongoingMatch.setAdvantage(player1);
            ongoingMatch.setAdvantage(null);
            assertNull(ongoingMatch.getAdvantage());
        }

        @Test
        void setAdvantage_switchPlayers_shouldUpdateAdvantage() {
            ongoingMatch.setAdvantage(player1);
            ongoingMatch.setAdvantage(player2);
            assertEquals(player2, ongoingMatch.getAdvantage());
        }
    }

    @Nested
    @DisplayName("Reset Points to Forty Tests")
    class ResetPointsToFortyTests {

        @Test
        void resetPointsToForty_fromAdvantage_shouldSetToForty() {
            // Setup: give player1 advantage
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardPointTo(player1);

            ongoingMatch.resetPointsToForty(player1);

            assertEquals(Points.FORTY, ongoingMatch.getPoints(player1));
        }

        @Test
        void resetPointsToForty_fromZero_shouldSetToForty() {
            ongoingMatch.resetPointsToForty(player1);
            assertEquals(Points.FORTY, ongoingMatch.getPoints(player1));
        }

        @Test
        void resetPointsToForty_shouldOnlyAffectSpecifiedPlayer() {
            ongoingMatch.awardPointTo(player2);

            ongoingMatch.resetPointsToForty(player1);

            assertEquals(Points.FORTY, ongoingMatch.getPoints(player1));
            assertEquals(Points.FIFTEEN, ongoingMatch.getPoints(player2));
        }
    }

    @Nested
    @DisplayName("Winner and Match Finish Tests")
    class WinnerTests {

        @Test
        void setWinner_player1_shouldSetWinner() {
            ongoingMatch.setWinner(player1);
            assertEquals(player1, ongoingMatch.getWinner());
        }

        @Test
        void setWinner_shouldMakeMatchFinished() {
            ongoingMatch.setWinner(player1);
            assertTrue(ongoingMatch.isFinished());
        }

        @Test
        void isFinished_noWinner_shouldReturnFalse() {
            assertFalse(ongoingMatch.isFinished());
        }

        @Test
        void setWinner_canChangeWinner() {
            ongoingMatch.setWinner(player1);
            ongoingMatch.setWinner(player2);
            assertEquals(player2, ongoingMatch.getWinner());
        }
    }

    @Nested
    @DisplayName("Integration Scenario Tests")
    class IntegrationScenarioTests {

        @Test
        void completeGame_scenario_shouldWorkCorrectly() {
            // Player1 wins game: 15-0, 30-0, 40-0, Game
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardGameTo(player1);

            assertEquals(1, ongoingMatch.getGames(player1));
            assertEquals(Points.ZERO, ongoingMatch.getPoints(player1));
            assertEquals(Points.ZERO, ongoingMatch.getPoints(player2));
        }

        @Test
        void completeSet_scenario_shouldWorkCorrectly() {
            // Simulate winning 6 games
            for (int i = 0; i < 6; i++) {
                ongoingMatch.awardGameTo(player1);
            }

            ongoingMatch.awardSetTo(player1);

            assertEquals(1, ongoingMatch.getSets(player1));
            assertEquals(0, ongoingMatch.getGames(player1));
            assertEquals(0, ongoingMatch.getGames(player2));
        }

        @Test
        void tiebreakScenario_shouldWorkCorrectly() {
            // Setup: 6-6 in games
            for (int i = 0; i < 6; i++) {
                ongoingMatch.awardGameTo(player1);
                ongoingMatch.awardGameTo(player2);
            }

            // Activate tiebreak
            ongoingMatch.setTieBreak(true);

            // Player1 wins tiebreak 7-5
            for (int i = 0; i < 7; i++) {
                ongoingMatch.awardTieBreakPointTo(player1);
            }
            for (int i = 0; i < 5; i++) {
                ongoingMatch.awardTieBreakPointTo(player2);
            }

            assertEquals(7, ongoingMatch.getTieBreakPoints(player1));
            assertEquals(5, ongoingMatch.getTieBreakPoints(player2));

            // Award game and set
            ongoingMatch.awardGameTo(player1);

            assertEquals(0, ongoingMatch.getTieBreakPoints(player1));
            assertEquals(0, ongoingMatch.getTieBreakPoints(player2));
            assertEquals(7, ongoingMatch.getGames(player1));
        }

        @Test
        void deuceAndAdvantageScenario_shouldWorkCorrectly() {
            // Setup: 40-40
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.awardPointTo(player2);
            ongoingMatch.awardPointTo(player2);
            ongoingMatch.awardPointTo(player2);

            assertEquals(Points.FORTY, ongoingMatch.getPoints(player1));
            assertEquals(Points.FORTY, ongoingMatch.getPoints(player2));

            // Player1 gets advantage
            ongoingMatch.awardPointTo(player1);
            ongoingMatch.setAdvantage(player1);

            assertEquals(Points.ADVANTAGE, ongoingMatch.getPoints(player1));
            assertEquals(player1, ongoingMatch.getAdvantage());

            // Reset to deuce
            ongoingMatch.resetPointsToForty(player1);
            ongoingMatch.setAdvantage(null);

            assertEquals(Points.FORTY, ongoingMatch.getPoints(player1));
            assertNull(ongoingMatch.getAdvantage());
        }

        @Test
        void completeMatch_scenario_shouldWorkCorrectly() {
            // Player1 wins 2 sets (best of 3)
            ongoingMatch.awardSetTo(player1);
            ongoingMatch.awardSetTo(player1);
            ongoingMatch.setWinner(player1);

            assertEquals(2, ongoingMatch.getSets(player1));
            assertEquals(player1, ongoingMatch.getWinner());
            assertTrue(ongoingMatch.isFinished());
        }
    }
}