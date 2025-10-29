package handler_test;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.ongoingmatch.OngoingMatch;
import com.scoreboard.service.scorecalculation.Points;
import com.scoreboard.service.scorecalculation.handler.game_handler.WithAdvantageGameHandler;
import com.scoreboard.service.scorecalculation.rules.GameRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WithAdvantageGameHandlerTest {
    private Player player1;
    private Player player2;
    private OngoingMatch match;
    private WithAdvantageGameHandler handler;

    @BeforeEach
    void setUp() {
        player1 = new Player("Roger Federer");
        player2 = new Player("Rafael Nadal");
        match = new OngoingMatch(player1, player2);
        handler = new WithAdvantageGameHandler();
    }

    @Nested
    @DisplayName("Regular Point Progression Tests")
    class RegularPointProgressionTests {

        @Test
        void handle_from0to15_shouldAwardPoint() {
            // Given: 0-0
            assertEquals(Points.ZERO, match.getPoints(player1));

            // When: используем публичный метод handle()
            handler.handle(match, player1);

            // Then
            assertEquals(Points.FIFTEEN, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void handle_from15to30_shouldAwardPoint() {
            // Given: 15-0
            match.awardPointTo(player1);

            // When
            handler.handle(match, player1);

            // Then
            assertEquals(Points.THIRTY, match.getPoints(player1));
        }

        @Test
        void handle_from30to40_shouldAwardPoint() {
            // Given: 30-0
            match.awardPointTo(player1);
            match.awardPointTo(player1);

            // When
            handler.handle(match, player1);

            // Then
            assertEquals(Points.FORTY, match.getPoints(player1));
        }
    }

    @Nested
    @DisplayName("Game Win Before Deuce Tests")
    class GameWinBeforeDeuceTests {

        @Test
        void handle_40vs0_shouldWinGame() {
            // Given: 40-0
            setupScore(Points.FORTY, Points.ZERO);

            // When
            handler.handle(match, player1);

            // Then
            assertEquals(1, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void handle_40vs15_shouldWinGame() {
            // Given: 40-15
            setupScore(Points.FORTY, Points.FIFTEEN);

            // When
            handler.handle(match, player1);

            // Then
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void handle_40vs30_shouldWinGame() {
            // Given: 40-30
            setupScore(Points.FORTY, Points.THIRTY);

            // When
            handler.handle(match, player1);

            // Then
            assertEquals(1, match.getGames(player1));
        }
    }

    @Nested
    @DisplayName("Deuce to Advantage Tests")
    class DeuceToAdvantageTests {

        @Test
        void handle_40vs40_player1Scores_shouldGetAdvantage() {
            // Given: 40-40 (Deuce)
            setupScore(Points.FORTY, Points.FORTY);

            // When: player1 scores
            handler.handle(match, player1);

            // Then: AD-40
            assertEquals(Points.ADVANTAGE, match.getPoints(player1));
            assertEquals(Points.FORTY, match.getPoints(player2));
            assertEquals(player1, match.getAdvantage());
        }

        @Test
        void handle_40vs40_player2Scores_shouldGetAdvantage() {
            // Given: 40-40 (Deuce)
            setupScore(Points.FORTY, Points.FORTY);

            // When: player2 scores
            handler.handle(match, player2);

            // Then: 40-AD
            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.ADVANTAGE, match.getPoints(player2));
            assertEquals(player2, match.getAdvantage());
        }
    }

    @Nested
    @DisplayName("Advantage to Game Win Tests")
    class AdvantageToGameWinTests {

        @Test
        void handle_advantagePlayer1_player1Scores_shouldWinGame() {
            // Given: AD-40
            setupScore(Points.ADVANTAGE, Points.FORTY);
            match.setAdvantage(player1);

            // When: player1 scores
            handler.handle(match, player1);

            // Then: Game won
            assertEquals(1, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
            assertNull(match.getAdvantage());
        }

        @Test
        void handle_advantagePlayer2_player2Scores_shouldWinGame() {
            // Given: 40-AD
            setupScore(Points.FORTY, Points.ADVANTAGE);
            match.setAdvantage(player2);

            // When: player2 scores
            handler.handle(match, player2);

            // Then: Game won
            assertEquals(0, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }
    }

    @Nested
    @DisplayName("Advantage Reset to Deuce Tests")
    class AdvantageResetTests {

        @Test
        void handle_player2HasAdvantage_player1Scores_shouldResetToDeuce() {
            // Given: 40-AD
            setupScore(Points.FORTY, Points.ADVANTAGE);
            match.setAdvantage(player2);

            // When: player1 scores
            handler.handle(match, player1);

            // Then: Back to 40-40 (Deuce)
            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.FORTY, match.getPoints(player2));
            assertNull(match.getAdvantage());
        }

        @Test
        void handle_player1HasAdvantage_player2Scores_shouldResetToDeuce() {
            // Given: AD-40
            setupScore(Points.ADVANTAGE, Points.FORTY);
            match.setAdvantage(player1);

            // When: player2 scores
            handler.handle(match, player2);

            // Then: Back to 40-40 (Deuce)
            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.FORTY, match.getPoints(player2));
            assertNull(match.getAdvantage());
        }
    }

    @Nested
    @DisplayName("Multiple Deuce Scenarios")
    class MultipleDeuceTests {

        @Test
        void handle_multipleDeuces_shouldHandleCorrectly() {
            // First deuce: 40-40
            setupScore(Points.FORTY, Points.FORTY);

            // Player1 gets advantage: AD-40
            handler.handle(match, player1);
            assertEquals(Points.ADVANTAGE, match.getPoints(player1));
            assertEquals(player1, match.getAdvantage());

            // Player2 brings back to deuce: 40-40
            handler.handle(match, player2);
            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.FORTY, match.getPoints(player2));
            assertNull(match.getAdvantage());

            // Player2 gets advantage: 40-AD
            handler.handle(match, player2);
            assertEquals(Points.ADVANTAGE, match.getPoints(player2));
            assertEquals(player2, match.getAdvantage());

            // Player1 brings back to deuce again: 40-40
            handler.handle(match, player1);
            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.FORTY, match.getPoints(player2));
            assertNull(match.getAdvantage());

            // Player1 gets advantage: AD-40
            handler.handle(match, player1);
            assertEquals(Points.ADVANTAGE, match.getPoints(player1));

            // Player1 wins game
            handler.handle(match, player1);
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void handle_longDeuceSequence_shouldEventuallyWin() {
            // Setup: 40-40
            setupScore(Points.FORTY, Points.FORTY);

            // Simulate 5 deuces
            for (int i = 0; i < 5; i++) {
                // Player1 advantage
                handler.handle(match, player1);
                assertEquals(Points.ADVANTAGE, match.getPoints(player1));

                // Back to deuce
                handler.handle(match, player2);
                assertEquals(Points.FORTY, match.getPoints(player1));
                assertEquals(Points.FORTY, match.getPoints(player2));
            }

            // Finally player1 wins
            handler.handle(match, player1); // AD-40
            handler.handle(match, player1); // Game

            assertEquals(1, match.getGames(player1));
        }
    }

    @Nested
    @DisplayName("Complete Game Scenarios")
    class CompleteGameScenarioTests {

        @Test
        void completeGame_straightWin_40to0() {
            // 0-0 → 15-0 → 30-0 → 40-0 → Game
            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player1);

            assertEquals(1, match.getGames(player1));
        }

        @Test
        void completeGame_withDeuce() {
            // 0-0 → 15-0
            handler.handle(match, player1);
            // 15-0 → 15-15
            handler.handle(match, player2);
            // 15-15 → 30-15
            handler.handle(match, player1);
            // 30-15 → 30-30
            handler.handle(match, player2);
            // 30-30 → 40-30
            handler.handle(match, player1);
            // 40-30 → 40-40
            handler.handle(match, player2);

            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.FORTY, match.getPoints(player2));

            // 40-40 → AD-40
            handler.handle(match, player1);
            assertEquals(Points.ADVANTAGE, match.getPoints(player1));

            // AD-40 → Game
            handler.handle(match, player1);
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void completeGame_player2Wins() {
            // Player2 wins: 0-40 → Game
            handler.handle(match, player2);
            handler.handle(match, player2);
            handler.handle(match, player2);
            handler.handle(match, player2);

            assertEquals(0, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }
    }

    @Nested
    @DisplayName("Match Finished Tests")
    class MatchFinishedTests {

        @Test
        void handle_matchAlreadyFinished_shouldNotProcessPoint() {
            // Given: match is finished
            match.setWinner(player1);
            int initialGames = match.getGames(player1);
            Points initialPoints = match.getPoints(player1);

            // When: try to award point
            handler.handle(match, player1);

            // Then: nothing should change
            assertEquals(initialGames, match.getGames(player1));
            assertEquals(initialPoints, match.getPoints(player1));
        }
    }

    @Nested
    @DisplayName("Tiebreak Mode Tests")
    class TiebreakModeTests {

        @Test
        void handle_tiebreakActive_shouldNotProcessInGameHandler() {
            // Given: tiebreak is active
            match.setTieBreak(true);
            Points initialPoints = match.getPoints(player1);

            // When: try to award point
            handler.handle(match, player1);

            // Then: points should not change (handled by TiebreakHandler)
            assertEquals(initialPoints, match.getPoints(player1));
        }
    }

    // Helper method
    private void setupScore(Points player1Points, Points player2Points) {
        match = new OngoingMatch(player1, player2);

        while (match.getPoints(player1) != player1Points) {
            match.awardPointTo(player1);
        }
        while (match.getPoints(player2) != player2Points) {
            match.awardPointTo(player2);
        }
    }

    // Test GameRules implementation
    private static class TestGameRules implements GameRules {
        @Override
        public boolean isAdvantageEnabled() {
            return true;
        }
    }
}
