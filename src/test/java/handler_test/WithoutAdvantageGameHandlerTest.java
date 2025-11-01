package handler_test;

import com.scoreboard.model.entity.Player;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.service.scorecalculation.Points;
import com.scoreboard.service.scorecalculation.handler.WithoutAdvantageGameHandler;
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
            assertEquals(Points.ZERO, match.getPoints(player1));

            handler.handle(match, player1);

            assertEquals(Points.FIFTEEN, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void handle_from15to30_shouldAwardPoint() {
            match.awardPointTo(player1);

            handler.handle(match, player1);

            assertEquals(Points.THIRTY, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void handle_from30to40_shouldAwardPoint() {
            match.awardPointTo(player1);
            match.awardPointTo(player1);

            handler.handle(match, player1);

            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void handle_bothPlayersScoring_shouldTrackSeparately() {

            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player1);

            assertEquals(Points.THIRTY, match.getPoints(player1));
            assertEquals(Points.FIFTEEN, match.getPoints(player2));
        }

        @Test
        void handle_progressionToForty_shouldWork() {
            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player1);

            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }
    }

    @Nested
    @DisplayName("Game Win Tests - Regular Scenarios")
    class GameWinRegularTests {

        @Test
        void handle_40vs0_shouldWinGame() {
            setupScore(Points.FORTY, Points.ZERO);

            handler.handle(match, player1);

            assertEquals(1, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void handle_40vs15_shouldWinGame() {
            setupScore(Points.FORTY, Points.FIFTEEN);

            handler.handle(match, player1);

            assertEquals(1, match.getGames(player1));
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void handle_40vs30_shouldWinGame() {
            setupScore(Points.FORTY, Points.THIRTY);

            handler.handle(match, player1);

            assertEquals(1, match.getGames(player1));
        }

        @Test
        void handle_0vs40_player2Scores_shouldWinGame() {
            setupScore(Points.ZERO, Points.FORTY);

            handler.handle(match, player2);

            assertEquals(0, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }

        @Test
        void handle_15vs40_player2Scores_shouldWinGame() {
            setupScore(Points.FIFTEEN, Points.FORTY);

            handler.handle(match, player2);

            assertEquals(1, match.getGames(player2));
        }

        @Test
        void handle_30vs40_player2Scores_shouldWinGame() {
            setupScore(Points.THIRTY, Points.FORTY);

            handler.handle(match, player2);

            assertEquals(1, match.getGames(player2));
        }
    }

    @Nested
    @DisplayName("Sudden Death at 40-40 (No-Ad Deuce)")
    class SuddenDeathTests {

        @Test
        void handle_40vs40_player1Scores_shouldWinGameImmediately() {
            setupScore(Points.FORTY, Points.FORTY);

            handler.handle(match, player1);

            assertEquals(1, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void handle_40vs40_player2Scores_shouldWinGameImmediately() {
            setupScore(Points.FORTY, Points.FORTY);

            handler.handle(match, player2);

            assertEquals(0, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }

        @Test
        void handle_40vs40_multipleGames_shouldWorkConsistently() {
            playToFortyForty();
            handler.handle(match, player1);
            assertEquals(1, match.getGames(player1));
            assertEquals(0, match.getGames(player2));

            playToFortyForty();
            handler.handle(match, player2);
            assertEquals(1, match.getGames(player1));
            assertEquals(1, match.getGames(player2));

            playToFortyForty();
            handler.handle(match, player1);
            assertEquals(2, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }
    }


    private void playToFortyForty() {
        handler.handle(match, player1);
        handler.handle(match, player1);
        handler.handle(match, player1);
        handler.handle(match, player2);
        handler.handle(match, player2);
        handler.handle(match, player2);
    }

    @Nested
    @DisplayName("Complete Game Scenarios")
    class CompleteGameScenarioTests {

        @Test
        void completeGame_straightWin_40to0() {
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
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player1);

            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.THIRTY, match.getPoints(player2));

            handler.handle(match, player1); // Game
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void completeGame_withDeuceAndSuddenDeath() {
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player1);
            handler.handle(match, player2);

            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.FORTY, match.getPoints(player2));

            handler.handle(match, player1);
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void completeGame_player2Wins() {
            handler.handle(match, player2);
            handler.handle(match, player2);
            handler.handle(match, player2);
            handler.handle(match, player2);

            assertEquals(0, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }

        @Test
        void completeGame_backAndForth() {
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player2);
            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player2);

            assertEquals(0, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }
    }

    @Nested
    @DisplayName("Multiple Games Scenarios")
    class MultipleGamesTests {

        @Test
        void handle_multipleGamesInSet_shouldAccumulate() {
            playGameToScore(player1, 4, 0);
            assertEquals(1, match.getGames(player1));

            playGameToScore(player2, 4, 0);
            assertEquals(1, match.getGames(player1));
            assertEquals(1, match.getGames(player2));

            playGameToScore(player1, 4, 0);
            assertEquals(2, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }

        @Test
        void handle_pointsResetAfterEachGame() {
            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player1);

            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));

            handler.handle(match, player2);
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.FIFTEEN, match.getPoints(player2));
        }
    }

    @Nested
    @DisplayName("Edge Cases and State Verification")
    class EdgeCasesTests {

        @Test
        void handle_beforeForty_shouldNotWinGame() {
            setupScore(Points.THIRTY, Points.ZERO);

            handler.handle(match, player1);

            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(0, match.getGames(player1));
        }

        @Test
        void handle_0vs30_player1Scores_shouldOnlyAwardPoint() {
            setupScore(Points.ZERO, Points.THIRTY);

            handler.handle(match, player1);

            assertEquals(Points.FIFTEEN, match.getPoints(player1));
            assertEquals(Points.THIRTY, match.getPoints(player2));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
        }

        @Test
        void handle_afterGameWon_pointsShouldBeZero() {
            setupScore(Points.FORTY, Points.ZERO);

            handler.handle(match, player1);

            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }
    }

    @Nested
    @DisplayName("Match Finished Tests")
    class MatchFinishedTests {

        @Test
        void handle_matchAlreadyFinished_shouldNotProcessPoint() {
            match.setWinner(player1);
            int initialGames = match.getGames(player1);
            Points initialPoints = match.getPoints(player1);

            handler.handle(match, player1);

            assertEquals(initialGames, match.getGames(player1));
            assertEquals(initialPoints, match.getPoints(player1));
        }

        @Test
        void handle_afterMatchFinished_multipleAttempts_shouldNotChange() {
            match.setWinner(player2);

            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player2);

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
            match.setTieBreak(true);
            Points initialPlayer1Points = match.getPoints(player1);
            Points initialPlayer2Points = match.getPoints(player2);
            int initialGames = match.getGames(player1);

            handler.handle(match, player1);

            assertEquals(initialPlayer1Points, match.getPoints(player1));
            assertEquals(initialPlayer2Points, match.getPoints(player2));
            assertEquals(initialGames, match.getGames(player1));
        }

        @Test
        void handle_tiebreakActive_noPointsAwarded() {
            match.setTieBreak(true);
            match.awardPointTo(player1);
            match.awardPointTo(player1);

            handler.handle(match, player1);

            assertEquals(Points.THIRTY, match.getPoints(player1));
        }
    }

    @Nested
    @DisplayName("No Advantage Verification")
    class NoAdvantageVerificationTests {

        @Test
        void handle_shouldNeverSetAdvantage() {
            for (int i = 0; i < 5; i++) {
                setupScore(Points.FORTY, Points.FORTY);
                handler.handle(match, player1);

                assertNull(match.getAdvantage());
            }
        }

        @Test
        void handle_40vs40_advantageShouldRemainNull() {
            setupScore(Points.FORTY, Points.FORTY);
            assertNull(match.getAdvantage());

            handler.handle(match, player1);

            assertNull(match.getAdvantage());
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void handle_pointsShouldNeverReachAdvantage() {
            for (int game = 0; game < 10; game++) {
                for (int point = 0; point < 10; point++) {
                    Player scorer = (point % 2 == 0) ? player1 : player2;
                    handler.handle(match, scorer);

                    assertNotEquals(Points.ADVANTAGE, match.getPoints(player1));
                    assertNotEquals(Points.ADVANTAGE, match.getPoints(player2));

                    if (match.getGames(player1) > game || match.getGames(player2) > game) {
                        break;
                    }
                }
            }
        }
    }

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
}
