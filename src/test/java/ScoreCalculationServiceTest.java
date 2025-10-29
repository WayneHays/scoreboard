import com.scoreboard.model.entity.Player;
import com.scoreboard.model.ongoingmatch.OngoingMatch;
import com.scoreboard.service.scorecalculation.Points;
import com.scoreboard.service.scorecalculation.ScoreCalculationService;
import com.scoreboard.service.scorecalculation.rules.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreCalculationServiceTest {
    private Player player1;
    private Player player2;
    private OngoingMatch match;
    private ScoreCalculationService service;

    @BeforeEach
    void setUp() {
        player1 = new Player("Rafael Nadal");
        player2 = new Player("Novak Djokovic");
        match = new OngoingMatch(player1, player2);
        service = new ScoreCalculationService();
    }

    @Nested
    @DisplayName("Service Initialization Tests")
    class ServiceInitializationTests {

        @Test
        void constructor_noArgs_shouldUseStandardRules() {
            ScoreCalculationService defaultService = new ScoreCalculationService();
            OngoingMatch testMatch = new OngoingMatch(player1, player2);

            defaultService.awardPoint(testMatch, player1);

            assertEquals(Points.FIFTEEN, testMatch.getPoints(player1));
        }

        @Test
        void constructor_withCustomRules_shouldUseCustomRules() {
            TennisMatchRules noAdRules = TennisMatchRules.custom(
                    new NoAdGameRules(),
                    new StandardSetRules(),
                    new StandardMatchRules(),
                    new StandardTiebreakRules()
            );

            ScoreCalculationService customService = new ScoreCalculationService(noAdRules);

            OngoingMatch testMatch = new OngoingMatch(player1, player2);

            // Play to 40-40
            awardPoints(customService, testMatch, player1, 3);
            awardPoints(customService, testMatch, player2, 3);

            // Next point wins (no-ad)
            customService.awardPoint(testMatch, player1);

            assertEquals(1, testMatch.getGames(player1));
        }
    }

    @Nested
    @DisplayName("Basic Point Award Tests")
    class BasicPointTests {

        @Test
        void awardPoint_singlePoint_shouldIncrease() {
            service.awardPoint(match, player1);

            assertEquals(Points.FIFTEEN, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void awardPoint_multiplePoints_shouldAccumulate() {
            service.awardPoint(match, player1); // 15
            service.awardPoint(match, player1); // 30
            service.awardPoint(match, player1); // 40

            assertEquals(Points.FORTY, match.getPoints(player1));
        }

        @Test
        void awardPoint_alternatingPlayers_shouldTrackSeparately() {
            service.awardPoint(match, player1); // 15-0
            service.awardPoint(match, player2); // 15-15
            service.awardPoint(match, player1); // 30-15

            assertEquals(Points.THIRTY, match.getPoints(player1));
            assertEquals(Points.FIFTEEN, match.getPoints(player2));
        }
    }

    @Nested
    @DisplayName("Game Tests")
    class GameTests {

        @Test
        void awardPoint_winGame_straightWin() {
            // Win game 4-0
            awardPoints(service, match, player1, 4);

            assertEquals(1, match.getGames(player1));
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void awardPoint_winGame_withDeuce() {
            // Play to 40-40
            awardPoints(service, match, player1, 3);
            awardPoints(service, match, player2, 3);

            // Player1 advantage
            service.awardPoint(match, player1);
            assertEquals(player1, match.getAdvantage());

            // Back to deuce
            service.awardPoint(match, player2);
            assertNull(match.getAdvantage());

            // Player1 wins
            service.awardPoint(match, player1);
            service.awardPoint(match, player1);

            assertEquals(1, match.getGames(player1));
        }

        @Test
        void awardPoint_multipleGames_shouldAccumulate() {
            // Game 1
            awardPoints(service, match, player1, 4);
            assertEquals(1, match.getGames(player1));

            // Game 2
            awardPoints(service, match, player2, 4);
            assertEquals(1, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }
    }

    @Nested
    @DisplayName("Set Tests")
    class SetTests {

        @Test
        void awardPoint_winSet_6to0() {
            // Win 6 games
            for (int i = 0; i < 6; i++) {
                awardPoints(service, match, player1, 4);
            }

            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
        }

        @Test
        void awardPoint_winSet_6to4() {
            // Player1: 6 games, Player2: 4 games
            for (int i = 0; i < 6; i++) {
                awardPoints(service, match, player1, 4);
            }
            for (int i = 0; i < 4; i++) {
                awardPoints(service, match, player2, 4);
            }

            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getGames(player1));
        }

        @Test
        void awardPoint_winSet_7to5() {
            // Play alternating to 5-5
            for (int i = 0; i < 5; i++) {
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player2, 4);
            }

            // Player1 wins 6-5
            awardPoints(service, match, player1, 4);
            assertEquals(0, match.getSets(player1)); // Not won yet

            // Player1 wins 7-5
            awardPoints(service, match, player1, 4);
            assertEquals(1, match.getSets(player1));
        }

        @Test
        void awardPoint_notWinSet_6to5() {
            // Play to 5-5 first (alternating)
            for (int i = 0; i < 5; i++) {
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player2, 4);
            }

            // Now 5-5, player1 wins one more game to make it 6-5
            awardPoints(service, match, player1, 4);

            // Verify 6-5 (set not won - need 2 game difference)
            assertEquals(0, match.getSets(player1), "Set should not be won yet");
            assertEquals(6, match.getGames(player1));
            assertEquals(5, match.getGames(player2));
        }
    }

    @Nested
    @DisplayName("Tiebreak Tests")
    class TiebreakTests {

        @Test
        void awardPoint_tiebreakActivation_at6to6() {
            // Play to 6-6
            for (int i = 0; i < 6; i++) {
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player2, 4);
            }

            assertTrue(match.isTieBreak());
            assertEquals(6, match.getGames(player1));
            assertEquals(6, match.getGames(player2));
        }

        @Test
        void awardPoint_tiebreakWin_7to5() {
            // Play to 6-6
            for (int i = 0; i < 6; i++) {
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player2, 4);
            }

            assertTrue(match.isTieBreak());
            assertEquals(6, match.getGames(player1));
            assertEquals(6, match.getGames(player2));

            // Tiebreak to 6-5 (not finished yet)
            for (int i = 0; i < 6; i++) {
                service.awardPoint(match, player1);
            }
            for (int i = 0; i < 5; i++) {
                service.awardPoint(match, player2);
            }

            assertTrue(match.isTieBreak()); // Still in tiebreak

            // Player1 scores winning point (7-5)
            service.awardPoint(match, player1);

            // Now tiebreak is finished and set is won
            assertFalse(match.isTieBreak());
            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getGames(player1)); // Games reset after set win
            assertEquals(0, match.getGames(player2));
        }

        @Test
        void awardPoint_tiebreak_8to6() {
            // Play to 6-6
            for (int i = 0; i < 6; i++) {
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player2, 4);
            }

            // Tiebreak to 6-6
            for (int i = 0; i < 6; i++) {
                service.awardPoint(match, player1);
                service.awardPoint(match, player2);
            }

            assertTrue(match.isTieBreak());

            // Player1 wins 8-6
            service.awardPoint(match, player1);
            service.awardPoint(match, player1);

            assertFalse(match.isTieBreak());
            assertEquals(1, match.getSets(player1));
        }
    }

    @Nested
    @DisplayName("Match Tests - Best of 3")
    class MatchBestOf3Tests {

        @Test
        void awardPoint_winMatch_2to0() {
            // Set 1: 6-0
            for (int i = 0; i < 6; i++) {
                awardPoints(service, match, player1, 4);
            }
            assertEquals(1, match.getSets(player1));
            assertFalse(match.isFinished());

            // Set 2: 6-0
            for (int i = 0; i < 6; i++) {
                awardPoints(service, match, player1, 4);
            }

            assertEquals(2, match.getSets(player1));
            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
        }

        @Test
        void awardPoint_winMatch_2to1() {
            // Set 1: player1 wins 6-3
            playGames(player2, 3);
            playGames(player1, 6);
            assertEquals(1, match.getSets(player1));

            // Set 2: player2 wins 6-4
            playGames(player1, 4);
            playGames(player2, 6);
            assertEquals(1, match.getSets(player1));
            assertEquals(1, match.getSets(player2));

            assertFalse(match.isFinished());

            // Set 3: player1 wins 6-2
            playGames(player2, 2);
            playGames(player1, 6);

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
            assertEquals(2, match.getSets(player1));
        }
    }

    @Nested
    @DisplayName("Match Tests - Best of 5")
    class MatchBestOf5Tests {

        @BeforeEach
        void setupBestOfFive() {
            TennisMatchRules rules = TennisMatchRules.custom(
                    new StandardGameRules(),
                    new StandardSetRules(),
                    new BestOfFiveMatchRules(),
                    new StandardTiebreakRules()
            );

            service = new ScoreCalculationService(rules);
        }

        @Test
        void awardPoint_winMatch_3to0() {
            // 3 sets
            for (int set = 0; set < 3; set++) {
                for (int i = 0; i < 6; i++) {
                    awardPoints(service, match, player1, 4);
                }
            }

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
            assertEquals(3, match.getSets(player1));
        }

        @Test
        void awardPoint_winMatch_3to2() {
            // Set 1: player1 wins 6-0
            playGames(player1, 6);

            // Set 2: player2 wins 6-0
            playGames(player2, 6);

            // Set 3: player1 wins 6-0
            playGames(player1, 6);

            // Set 4: player2 wins 6-0
            playGames(player2, 6);

            assertEquals(2, match.getSets(player1));
            assertEquals(2, match.getSets(player2));
            assertFalse(match.isFinished());

            // Set 5: player1 wins 6-0
            playGames(player1, 6);

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
            assertEquals(3, match.getSets(player1));
        }
    }

    @Nested
    @DisplayName("Match Finished Tests")
    class MatchFinishedTests {

        @Test
        void awardPoint_afterMatchFinished_shouldNotChangeScore() {
            // Win match 2-0
            for (int set = 0; set < 2; set++) {
                for (int i = 0; i < 6; i++) {
                    awardPoints(service, match, player1, 4);
                }
            }

            assertTrue(match.isFinished());
            int finalSets = match.getSets(player1);

            // Try to award more points
            service.awardPoint(match, player2);
            service.awardPoint(match, player2);

            assertEquals(finalSets, match.getSets(player1));
            assertEquals(player1, match.getWinner());
        }
    }

    @Nested
    @DisplayName("Complete Match Scenarios")
    class CompleteMatchScenarios {

        @Test
        void completeMatch_simple_twoSets() {
            // Set 1: 6-4 (player1 wins)
            for (int i = 0; i < 4; i++) {
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player2, 4);
            }
            awardPoints(service, match, player1, 4); // 5-4
            awardPoints(service, match, player1, 4); // 6-4 SET

            assertEquals(1, match.getSets(player1));
            assertFalse(match.isFinished());

            // Set 2: 6-3 (player1 wins)
            for (int i = 0; i < 3; i++) {
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player2, 4);
            }
            awardPoints(service, match, player1, 4); // 4-3
            awardPoints(service, match, player1, 4); // 5-3
            awardPoints(service, match, player1, 4); // 6-3 SET

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
            assertEquals(2, match.getSets(player1));
        }

        @Test
        void completeMatch_realistic_withTiebreak() {
            // Set 1: 7-6 with tiebreak
            for (int i = 0; i < 6; i++) {
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player2, 4);
            }

            assertTrue(match.isTieBreak());

            // Tiebreak 7-5: просто чередуем очки явно
            service.awardPoint(match, player1); // 1-0
            service.awardPoint(match, player2); // 1-1
            service.awardPoint(match, player1); // 2-1
            service.awardPoint(match, player2); // 2-2
            service.awardPoint(match, player1); // 3-2
            service.awardPoint(match, player2); // 3-3
            service.awardPoint(match, player1); // 4-3
            service.awardPoint(match, player2); // 4-4
            service.awardPoint(match, player1); // 5-4
            service.awardPoint(match, player2); // 5-5
            service.awardPoint(match, player1); // 6-5
            service.awardPoint(match, player1); // 7-5 - TIEBREAK WON!

            assertFalse(match.isTieBreak());
            assertEquals(1, match.getSets(player1));

            // Set 2: 6-4
            for (int i = 0; i < 4; i++) {
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player2, 4);
            }
            awardPoints(service, match, player1, 4); // 5-4
            awardPoints(service, match, player1, 4); // 6-4

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
            assertEquals(2, match.getSets(player1));
        }

        @Test
        void completeMatch_comeback_1to2() {
            // Set 1: player1 wins 6-0
            playGames(player1, 6);
            assertEquals(1, match.getSets(player1));

            // Set 2: player2 wins 6-0
            playGames(player2, 6);
            assertEquals(1, match.getSets(player1));
            assertEquals(1, match.getSets(player2));
            assertFalse(match.isFinished());

            // Set 3: player2 wins 6-0
            playGames(player2, 6);

            assertTrue(match.isFinished());
            assertEquals(player2, match.getWinner());
            assertEquals(1, match.getSets(player1));
            assertEquals(2, match.getSets(player2));
        }

        @Test
        void completeMatch_closeSets() {
            // Set 1: 7-5 (player1 wins)
            for (int i = 0; i < 5; i++) {
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player2, 4);
            }
            awardPoints(service, match, player1, 4); // 6-5
            awardPoints(service, match, player1, 4); // 7-5

            assertEquals(1, match.getSets(player1));

            // Set 2: 6-4 (player1 wins)
            playGames(player2, 4);
            playGames(player1, 6);

            assertTrue(match.isFinished());
            assertEquals(player1, match.getWinner());
        }
    }

    @Nested
    @DisplayName("State Consistency Tests")
    class StateConsistencyTests {

        @Test
        void awardPoint_pointsResetAfterGame() {
            awardPoints(service, match, player1, 4);

            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
        }

        @Test
        void awardPoint_gamesResetAfterSet() {
            // Win 6 games straight
            for (int i = 0; i < 6; i++) {
                awardPoints(service, match, player1, 4);
            }

            assertEquals(1, match.getSets(player1));
            assertEquals(0, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
        }

        @Test
        void awardPoint_tiebreakPointsResetAfterTiebreak() {
            // Play to 6-6
            for (int i = 0; i < 6; i++) {
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player2, 4);
            }

            // Win tiebreak 7-5
            for (int i = 0; i < 7; i++) {
                service.awardPoint(match, player1);
            }
            for (int i = 0; i < 5; i++) {
                service.awardPoint(match, player2);
            }

            assertEquals(0, match.getTieBreakPoints(player1));
            assertEquals(0, match.getTieBreakPoints(player2));
        }
    }

    @Nested
    @DisplayName("No-Ad Format Tests")
    class NoAdFormatTests {

        @BeforeEach
        void setupNoAd() {
            TennisMatchRules rules = TennisMatchRules.custom(
                    new NoAdGameRules(),
                    new StandardSetRules(),
                    new StandardMatchRules(),
                    new StandardTiebreakRules()
            );

            service = new ScoreCalculationService(rules);
        }

        @Test
        void awardPoint_noAd_suddenDeathAt40_40() {
            // Play to 40-40
            awardPoints(service, match, player1, 3);
            awardPoints(service, match, player2, 3);

            // Next point wins
            service.awardPoint(match, player1);

            assertEquals(1, match.getGames(player1));
        }

        @Test
        void awardPoint_noAd_completeSet() {
            // Win 6 games
            for (int i = 0; i < 6; i++) {
                awardPoints(service, match, player1, 4);
            }

            assertEquals(1, match.getSets(player1));
        }
    }

    // Helper methods
    private void awardPoints(ScoreCalculationService svc, OngoingMatch m, Player p, int count) {
        for (int i = 0; i < count; i++) {
            svc.awardPoint(m, p);
        }
    }

    private void playGames(Player player, int count) {
        for (int i = 0; i < count; i++) {
            awardPoints(service, match, player, 4);
        }
    }

    // Test rules implementations
    private static class StandardGameRules implements GameRules {
        @Override
        public boolean isAdvantageEnabled() {
            return true;
        }
    }

    private static class NoAdGameRules implements GameRules {
        @Override
        public boolean isAdvantageEnabled() {
            return false;
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

    private static class StandardMatchRules implements MatchRules {
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
