package handler_test;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.ongoingmatch.OngoingMatch;
import com.scoreboard.service.scorecalculation.Points;
import com.scoreboard.service.scorecalculation.handler.game_handler.WithoutAdvantageGameHandler;
import com.scoreboard.service.scorecalculation.rules.GameRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WithoutAdvantageGameHandlerTest {
    private Player player1;
    private Player player2;
    private OngoingMatch match;
    private WithoutAdvantageGameHandler handler;

    @BeforeEach
    void setUp() {
        player1 = new Player("Novak Djokovic");
        player2 = new Player("Andy Murray");
        match = new OngoingMatch(player1, player2);
        handler = new WithoutAdvantageGameHandler();
    }

    @Nested
    @DisplayName("Regular Point Progression Tests")
    class RegularPointProgressionTests {

        @Test
        void handle_from0to15_shouldAwardPoint() {
            // Given: 0-0
            assertEquals(Points.ZERO, match.getPoints(player1));

            // When
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
            assertEquals(Points.ZERO, match.getPoints(player2));
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
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void handle_bothPlayersScoring_shouldTrackSeparately() {
            // Given: 0-0

            // When: alternating scores
            handler.handle(match, player1); // 15-0
            handler.handle(match, player2); // 15-15
            handler.handle(match, player1); // 30-15

            // Then
            assertEquals(Points.THIRTY, match.getPoints(player1));
            assertEquals(Points.FIFTEEN, match.getPoints(player2));
        }

        @Test
        void handle_progressionToForty_shouldWork() {
            // When: player1 scores 3 times
            handler.handle(match, player1); // 15-0
            handler.handle(match, player1); // 30-0
            handler.handle(match, player1); // 40-0

            // Then
            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }
    }

    @Nested
    @DisplayName("Game Win Tests - Regular Scenarios")
    class GameWinRegularTests {

        @Test
        void handle_40vs0_shouldWinGame() {
            // Given: 40-0
            setupScore(Points.FORTY, Points.ZERO);

            // When: player1 scores
            handler.handle(match, player1);

            // Then: Game won
            assertEquals(1, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void handle_40vs15_shouldWinGame() {
            // Given: 40-15
            setupScore(Points.FORTY, Points.FIFTEEN);

            // When: player1 scores
            handler.handle(match, player1);

            // Then: Game won
            assertEquals(1, match.getGames(player1));
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void handle_40vs30_shouldWinGame() {
            // Given: 40-30
            setupScore(Points.FORTY, Points.THIRTY);

            // When: player1 scores
            handler.handle(match, player1);

            // Then: Game won
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void handle_0vs40_player2Scores_shouldWinGame() {
            // Given: 0-40
            setupScore(Points.ZERO, Points.FORTY);

            // When: player2 scores
            handler.handle(match, player2);

            // Then: Game won by player2
            assertEquals(0, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }

        @Test
        void handle_15vs40_player2Scores_shouldWinGame() {
            // Given: 15-40
            setupScore(Points.FIFTEEN, Points.FORTY);

            // When: player2 scores
            handler.handle(match, player2);

            // Then: Game won by player2
            assertEquals(1, match.getGames(player2));
        }

        @Test
        void handle_30vs40_player2Scores_shouldWinGame() {
            // Given: 30-40
            setupScore(Points.THIRTY, Points.FORTY);

            // When: player2 scores
            handler.handle(match, player2);

            // Then: Game won by player2
            assertEquals(1, match.getGames(player2));
        }
    }

    @Nested
    @DisplayName("Sudden Death at 40-40 (No-Ad Deuce)")
    class SuddenDeathTests {

        @Test
        void handle_40vs40_player1Scores_shouldWinGameImmediately() {
            // Given: 40-40 (Deuce in no-ad format)
            setupScore(Points.FORTY, Points.FORTY);

            // When: player1 scores (sudden death point)
            handler.handle(match, player1);

            // Then: player1 wins game immediately (no advantage)
            assertEquals(1, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void handle_40vs40_player2Scores_shouldWinGameImmediately() {
            // Given: 40-40 (Deuce in no-ad format)
            setupScore(Points.FORTY, Points.FORTY);

            // When: player2 scores (sudden death point)
            handler.handle(match, player2);

            // Then: player2 wins game immediately (no advantage)
            assertEquals(0, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }

        @Test
        void handle_40vs40_multipleGames_shouldWorkConsistently() {
            // Game 1: Play to 40-40, player1 wins
            playToFortyForty();
            handler.handle(match, player1); // Sudden death - player1 wins
            assertEquals(1, match.getGames(player1));
            assertEquals(0, match.getGames(player2));

            // Game 2: Play to 40-40, player2 wins
            playToFortyForty();
            handler.handle(match, player2); // Sudden death - player2 wins
            assertEquals(1, match.getGames(player1));
            assertEquals(1, match.getGames(player2));

            // Game 3: Play to 40-40, player1 wins
            playToFortyForty();
            handler.handle(match, player1); // Sudden death - player1 wins
            assertEquals(2, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }
    }

    // Добавь этот helper метод в конец класса
    private void playToFortyForty() {
        // After a game, points are reset to 0-0
        // Play to 40-40
        handler.handle(match, player1); // 15-0
        handler.handle(match, player1); // 30-0
        handler.handle(match, player1); // 40-0
        handler.handle(match, player2); // 40-15
        handler.handle(match, player2); // 40-30
        handler.handle(match, player2); // 40-40
    }

    @Nested
    @DisplayName("Complete Game Scenarios")
    class CompleteGameScenarioTests {

        @Test
        void completeGame_straightWin_40to0() {
            // Scenario: Player1 wins 4 straight points
            // 0-0 → 15-0 → 30-0 → 40-0 → Game
            handler.handle(match, player1);
            assertEquals(Points.FIFTEEN, match.getPoints(player1));

            handler.handle(match, player1);
            assertEquals(Points.THIRTY, match.getPoints(player1));

            handler.handle(match, player1);
            assertEquals(Points.FORTY, match.getPoints(player1));

            handler.handle(match, player1);
            assertEquals(1, match.getGames(player1));
            assertEquals(Points.ZERO, match.getPoints(player1));
        }

        @Test
        void completeGame_40to30() {
            // Scenario: 0-0 → 15-0 → 15-15 → 30-15 → 30-30 → 40-30 → Game
            handler.handle(match, player1); // 15-0
            handler.handle(match, player2); // 15-15
            handler.handle(match, player1); // 30-15
            handler.handle(match, player2); // 30-30
            handler.handle(match, player1); // 40-30

            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.THIRTY, match.getPoints(player2));

            handler.handle(match, player1); // Game
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void completeGame_withDeuceAndSuddenDeath() {
            // Scenario: Game goes to 40-40, then sudden death
            handler.handle(match, player1); // 15-0
            handler.handle(match, player2); // 15-15
            handler.handle(match, player1); // 30-15
            handler.handle(match, player2); // 30-30
            handler.handle(match, player1); // 40-30
            handler.handle(match, player2); // 40-40

            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.FORTY, match.getPoints(player2));

            // Sudden death point
            handler.handle(match, player1); // Game (no advantage)
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void completeGame_player2Wins() {
            // Scenario: Player2 wins 4 straight points
            handler.handle(match, player2);
            handler.handle(match, player2);
            handler.handle(match, player2);
            handler.handle(match, player2);

            assertEquals(0, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }

        @Test
        void completeGame_backAndForth() {
            // Complex scenario: 15-0, 15-15, 15-30, 30-30, 40-30, 40-40, Game
            handler.handle(match, player1); // 15-0
            handler.handle(match, player2); // 15-15
            handler.handle(match, player2); // 15-30
            handler.handle(match, player1); // 30-30
            handler.handle(match, player1); // 40-30
            handler.handle(match, player2); // 40-40
            handler.handle(match, player2); // Game (player2 wins)

            assertEquals(0, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }
    }

    @Nested
    @DisplayName("Multiple Games Scenarios")
    class MultipleGamesTests {

        @Test
        void handle_multipleGamesInSet_shouldAccumulate() {
            // Game 1: player1 wins
            playGameToScore(player1, 4, 0);
            assertEquals(1, match.getGames(player1));

            // Game 2: player2 wins
            playGameToScore(player2, 4, 0);
            assertEquals(1, match.getGames(player1));
            assertEquals(1, match.getGames(player2));

            // Game 3: player1 wins
            playGameToScore(player1, 4, 0);
            assertEquals(2, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }

        @Test
        void handle_pointsResetAfterEachGame() {
            // Game 1
            handler.handle(match, player1); // 15-0
            handler.handle(match, player1); // 30-0
            handler.handle(match, player1); // 40-0
            handler.handle(match, player1); // Game

            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));

            // Game 2
            handler.handle(match, player2); // 0-15
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.FIFTEEN, match.getPoints(player2));
        }
    }

    @Nested
    @DisplayName("Edge Cases and State Verification")
    class EdgeCasesTests {

        @Test
        void handle_beforeForty_shouldNotWinGame() {
            // Given: 30-0
            setupScore(Points.THIRTY, Points.ZERO);

            // When: player1 scores
            handler.handle(match, player1);

            // Then: 40-0, but game not won yet
            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(0, match.getGames(player1));
        }

        @Test
        void handle_0vs30_player1Scores_shouldOnlyAwardPoint() {
            // Given: 0-30
            setupScore(Points.ZERO, Points.THIRTY);

            // When: player1 scores
            handler.handle(match, player1);

            // Then: 15-30, no game won
            assertEquals(Points.FIFTEEN, match.getPoints(player1));
            assertEquals(Points.THIRTY, match.getPoints(player2));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
        }

        @Test
        void handle_afterGameWon_pointsShouldBeZero() {
            // Given: 40-0
            setupScore(Points.FORTY, Points.ZERO);

            // When: player1 wins game
            handler.handle(match, player1);

            // Then: points reset to zero
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
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

        @Test
        void handle_afterMatchFinished_multipleAttempts_shouldNotChange() {
            // Given: match is finished
            match.setWinner(player2);

            // When: multiple attempts to award points
            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player2);

            // Then: state unchanged
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }
    }

    @Nested
    @DisplayName("Tiebreak Mode Tests")
    class TiebreakModeTests {

        @Test
        void handle_tiebreakActive_shouldNotProcessInGameHandler() {
            // Given: tiebreak is active
            match.setTieBreak(true);
            Points initialPlayer1Points = match.getPoints(player1);
            Points initialPlayer2Points = match.getPoints(player2);
            int initialGames = match.getGames(player1);

            // When: try to award point
            handler.handle(match, player1);

            // Then: nothing should change (handled by TiebreakHandler)
            assertEquals(initialPlayer1Points, match.getPoints(player1));
            assertEquals(initialPlayer2Points, match.getPoints(player2));
            assertEquals(initialGames, match.getGames(player1));
        }

        @Test
        void handle_tiebreakActive_noPointsAwarded() {
            // Given: tiebreak mode with some score
            match.setTieBreak(true);
            match.awardPointTo(player1);
            match.awardPointTo(player1);

            // When: handle is called
            handler.handle(match, player1);

            // Then: regular points don't change
            assertEquals(Points.THIRTY, match.getPoints(player1));
        }
    }

    @Nested
    @DisplayName("No Advantage Verification")
    class NoAdvantageVerificationTests {

        @Test
        void handle_shouldNeverSetAdvantage() {
            // Play multiple games with 40-40 scenarios
            for (int i = 0; i < 5; i++) {
                setupScore(Points.FORTY, Points.FORTY);
                handler.handle(match, player1);

                // Advantage should always be null in no-ad format
                assertNull(match.getAdvantage());
            }
        }

        @Test
        void handle_40vs40_advantageShouldRemainNull() {
            // Given: 40-40
            setupScore(Points.FORTY, Points.FORTY);
            assertNull(match.getAdvantage());

            // When: player scores
            handler.handle(match, player1);

            // Then: game won, no advantage ever set
            assertNull(match.getAdvantage());
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void handle_pointsShouldNeverReachAdvantage() {
            // Play many points
            for (int game = 0; game < 10; game++) {
                // Random points
                for (int point = 0; point < 10; point++) {
                    Player scorer = (point % 2 == 0) ? player1 : player2;
                    handler.handle(match, scorer);

                    // Verify no player ever has ADVANTAGE points
                    assertNotEquals(Points.ADVANTAGE, match.getPoints(player1));
                    assertNotEquals(Points.ADVANTAGE, match.getPoints(player2));

                    // Break if game finished
                    if (match.getGames(player1) > game || match.getGames(player2) > game) {
                        break;
                    }
                }
            }
        }
    }

    // Helper methods
    private void setupScore(Points player1Points, Points player2Points) {
        match = new OngoingMatch(player1, player2);

        while (match.getPoints(player1) != player1Points) {
            match.awardPointTo(player1);
        }
        while (match.getPoints(player2) != player2Points) {
            match.awardPointTo(player2);
        }
    }

    private void playGameToScore(Player winner, int winnerPoints, int loserPoints) {
        for (int i = 0; i < winnerPoints; i++) {
            handler.handle(match, winner);
        }
        Player loser = match.getOpponent(winner);
        for (int i = 0; i < loserPoints; i++) {
            handler.handle(match, loser);
        }
    }

    // Test GameRules implementation
    private static class TestGameRules implements GameRules {
        @Override
        public boolean isAdvantageEnabled() {
            return false;
        }
    }
}
