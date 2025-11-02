import com.scoreboard.model.entity.Player;
import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.tennisrules.TennisMatchRulesImpl;
import com.scoreboard.service.scorecalculation.Points;
import com.scoreboard.service.scorecalculation.ScoreCalculationService;
import com.scoreboard.tennisrules.standard.StandardGameRules;
import com.scoreboard.tennisrules.standard.StandardMatchRules;
import com.scoreboard.tennisrules.standard.StandardSetRules;
import com.scoreboard.tennisrules.standard.StandardTiebreakRules;
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
        service = new ScoreCalculationService(new TennisMatchRulesImpl(
                new StandardGameRules(),
                new StandardTiebreakRules(),
                new StandardSetRules(),
                new StandardMatchRules()
        ));
    }

    @Nested
    @DisplayName("Service Initialization Tests")
    class ServiceInitializationTests {

        @Test
        void constructor_noArgs_shouldUseStandardRules() {
            OngoingMatch testMatch = new OngoingMatch(player1, player2);

            service.awardPoint(testMatch, player1);

            assertEquals(Points.FIFTEEN, testMatch.getPoints(player1));
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
                service.awardPoint(match, player1);
                service.awardPoint(match, player1);
                service.awardPoint(match, player1);

                assertEquals(Points.FORTY, match.getPoints(player1));
            }

            @Test
            void awardPoint_alternatingPlayers_shouldTrackSeparately() {
                service.awardPoint(match, player1);
                service.awardPoint(match, player2);
                service.awardPoint(match, player1);

                assertEquals(Points.THIRTY, match.getPoints(player1));
                assertEquals(Points.FIFTEEN, match.getPoints(player2));
            }
        }

        @Nested
        @DisplayName("Game Tests")
        class GameTests {

            @Test
            void awardPoint_winGame_straightWin() {
                awardPoints(service, match, player1, 4);

                assertEquals(1, match.getGames(player1));
                assertEquals(Points.ZERO, match.getPoints(player1));
                assertEquals(Points.ZERO, match.getPoints(player2));
            }

            @Test
            void awardPoint_winGame_withDeuce() {
                awardPoints(service, match, player1, 3);
                awardPoints(service, match, player2, 3);

                service.awardPoint(match, player1);
                assertEquals(player1, match.getAdvantage());

                service.awardPoint(match, player2);
                assertNull(match.getAdvantage());

                service.awardPoint(match, player1);
                service.awardPoint(match, player1);

                assertEquals(1, match.getGames(player1));
            }

            @Test
            void awardPoint_multipleGames_shouldAccumulate() {
                awardPoints(service, match, player1, 4);
                assertEquals(1, match.getGames(player1));

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
                for (int i = 0; i < 6; i++) {
                    awardPoints(service, match, player1, 4);
                }

                assertEquals(1, match.getSets(player1));
                assertEquals(0, match.getGames(player1));
                assertEquals(0, match.getGames(player2));
            }

            @Test
            void awardPoint_winSet_6to4() {
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
                for (int i = 0; i < 5; i++) {
                    awardPoints(service, match, player1, 4);
                    awardPoints(service, match, player2, 4);
                }

                awardPoints(service, match, player1, 4);
                assertEquals(0, match.getSets(player1));

                awardPoints(service, match, player1, 4);
                assertEquals(1, match.getSets(player1));
            }

            @Test
            void awardPoint_notWinSet_6to5() {
                for (int i = 0; i < 5; i++) {
                    awardPoints(service, match, player1, 4);
                    awardPoints(service, match, player2, 4);
                }

                awardPoints(service, match, player1, 4);

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
                for (int i = 0; i < 6; i++) {
                    awardPoints(service, match, player1, 4);
                    awardPoints(service, match, player2, 4);
                }

                assertTrue(match.isTieBreak());
                assertEquals(6, match.getGames(player1));
                assertEquals(6, match.getGames(player2));

                for (int i = 0; i < 6; i++) {
                    service.awardPoint(match, player1);
                }
                for (int i = 0; i < 5; i++) {
                    service.awardPoint(match, player2);
                }

                assertTrue(match.isTieBreak());

                service.awardPoint(match, player1);

                assertFalse(match.isTieBreak());
                assertEquals(1, match.getSets(player1));
                assertEquals(0, match.getGames(player1));
                assertEquals(0, match.getGames(player2));
            }

            @Test
            void awardPoint_tiebreak_8to6() {
                for (int i = 0; i < 6; i++) {
                    awardPoints(service, match, player1, 4);
                    awardPoints(service, match, player2, 4);
                }

                for (int i = 0; i < 6; i++) {
                    service.awardPoint(match, player1);
                    service.awardPoint(match, player2);
                }

                assertTrue(match.isTieBreak());

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
                for (int i = 0; i < 6; i++) {
                    awardPoints(service, match, player1, 4);
                }
                assertEquals(1, match.getSets(player1));
                assertFalse(match.isFinished());

                for (int i = 0; i < 6; i++) {
                    awardPoints(service, match, player1, 4);
                }

                assertEquals(2, match.getSets(player1));
                assertTrue(match.isFinished());
                assertEquals(player1, match.getWinner());
            }

            @Test
            void awardPoint_winMatch_2to1() {
                playGames(player2, 3);
                playGames(player1, 6);
                assertEquals(1, match.getSets(player1));

                playGames(player1, 4);
                playGames(player2, 6);
                assertEquals(1, match.getSets(player1));
                assertEquals(1, match.getSets(player2));

                assertFalse(match.isFinished());

                playGames(player2, 2);
                playGames(player1, 6);

                assertTrue(match.isFinished());
                assertEquals(player1, match.getWinner());
                assertEquals(2, match.getSets(player1));
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
                for (int i = 0; i < 4; i++) {
                    awardPoints(service, match, player1, 4);
                    awardPoints(service, match, player2, 4);
                }
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player1, 4);

                assertEquals(1, match.getSets(player1));
                assertFalse(match.isFinished());


                for (int i = 0; i < 3; i++) {
                    awardPoints(service, match, player1, 4);
                    awardPoints(service, match, player2, 4);
                }
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player1, 4);

                assertTrue(match.isFinished());
                assertEquals(player1, match.getWinner());
                assertEquals(2, match.getSets(player1));
            }

            @Test
            void completeMatch_realistic_withTiebreak() {
                for (int i = 0; i < 6; i++) {
                    awardPoints(service, match, player1, 4);
                    awardPoints(service, match, player2, 4);
                }

                assertTrue(match.isTieBreak());

                service.awardPoint(match, player1);
                service.awardPoint(match, player2);
                service.awardPoint(match, player1);
                service.awardPoint(match, player2);
                service.awardPoint(match, player1);
                service.awardPoint(match, player2);
                service.awardPoint(match, player1);
                service.awardPoint(match, player2);
                service.awardPoint(match, player1);
                service.awardPoint(match, player2);
                service.awardPoint(match, player1);
                service.awardPoint(match, player1);

                assertFalse(match.isTieBreak());
                assertEquals(1, match.getSets(player1));

                for (int i = 0; i < 4; i++) {
                    awardPoints(service, match, player1, 4);
                    awardPoints(service, match, player2, 4);
                }
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player1, 4);

                assertTrue(match.isFinished());
                assertEquals(player1, match.getWinner());
                assertEquals(2, match.getSets(player1));
            }

            @Test
            void completeMatch_comeback_1to2() {
                playGames(player1, 6);
                assertEquals(1, match.getSets(player1));

                playGames(player2, 6);
                assertEquals(1, match.getSets(player1));
                assertEquals(1, match.getSets(player2));
                assertFalse(match.isFinished());

                playGames(player2, 6);

                assertTrue(match.isFinished());
                assertEquals(player2, match.getWinner());
                assertEquals(1, match.getSets(player1));
                assertEquals(2, match.getSets(player2));
            }

            @Test
            void completeMatch_closeSets() {
                for (int i = 0; i < 5; i++) {
                    awardPoints(service, match, player1, 4);
                    awardPoints(service, match, player2, 4);
                }
                awardPoints(service, match, player1, 4);
                awardPoints(service, match, player1, 4);

                assertEquals(1, match.getSets(player1));

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
                for (int i = 0; i < 6; i++) {
                    awardPoints(service, match, player1, 4);
                }

                assertEquals(1, match.getSets(player1));
                assertEquals(0, match.getGames(player1));
                assertEquals(0, match.getGames(player2));
            }

            @Test
            void awardPoint_tiebreakPointsResetAfterTiebreak() {
                for (int i = 0; i < 6; i++) {
                    awardPoints(service, match, player1, 4);
                    awardPoints(service, match, player2, 4);
                }

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
    }
}
