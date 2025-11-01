package handler_test;

import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.model.entity.Player;
import com.scoreboard.service.scorecalculation.Points;
import com.scoreboard.service.scorecalculation.handler.WithAdvantageGameHandler;
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
        }

        @Test
        void handle_from30to40_shouldAwardPoint() {
            match.awardPointTo(player1);
            match.awardPointTo(player1);

            handler.handle(match, player1);

            assertEquals(Points.FORTY, match.getPoints(player1));
        }
    }

    @Nested
    @DisplayName("Game Win Before Deuce Tests")
    class GameWinBeforeDeuceTests {

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
        }

        @Test
        void handle_40vs30_shouldWinGame() {
            setupScore(Points.FORTY, Points.THIRTY);

            handler.handle(match, player1);

            assertEquals(1, match.getGames(player1));
        }
    }

    @Nested
    @DisplayName("Deuce to Advantage Tests")
    class DeuceToAdvantageTests {

        @Test
        void handle_40vs40_player1Scores_shouldGetAdvantage() {
            setupScore(Points.FORTY, Points.FORTY);

            handler.handle(match, player1);

            assertEquals(Points.ADVANTAGE, match.getPoints(player1));
            assertEquals(Points.FORTY, match.getPoints(player2));
            assertEquals(player1, match.getAdvantage());
        }

        @Test
        void handle_40vs40_player2Scores_shouldGetAdvantage() {
            setupScore(Points.FORTY, Points.FORTY);

            handler.handle(match, player2);

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
            setupScore(Points.ADVANTAGE, Points.FORTY);
            match.setAdvantage(player1);

            handler.handle(match, player1);

            assertEquals(1, match.getGames(player1));
            assertEquals(0, match.getGames(player2));
            assertEquals(Points.ZERO, match.getPoints(player1));
            assertEquals(Points.ZERO, match.getPoints(player2));
            assertNull(match.getAdvantage());
        }

        @Test
        void handle_advantagePlayer2_player2Scores_shouldWinGame() {
            setupScore(Points.FORTY, Points.ADVANTAGE);
            match.setAdvantage(player2);

            handler.handle(match, player2);

            assertEquals(0, match.getGames(player1));
            assertEquals(1, match.getGames(player2));
        }
    }

    @Nested
    @DisplayName("Advantage Reset to Deuce Tests")
    class AdvantageResetTests {

        @Test
        void handle_player2HasAdvantage_player1Scores_shouldResetToDeuce() {
            setupScore(Points.FORTY, Points.ADVANTAGE);
            match.setAdvantage(player2);

            handler.handle(match, player1);

            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.FORTY, match.getPoints(player2));
            assertNull(match.getAdvantage());
        }

        @Test
        void handle_player1HasAdvantage_player2Scores_shouldResetToDeuce() {
            setupScore(Points.ADVANTAGE, Points.FORTY);
            match.setAdvantage(player1);

            handler.handle(match, player2);

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
            setupScore(Points.FORTY, Points.FORTY);

            handler.handle(match, player1);
            assertEquals(Points.ADVANTAGE, match.getPoints(player1));
            assertEquals(player1, match.getAdvantage());

            handler.handle(match, player2);
            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.FORTY, match.getPoints(player2));
            assertNull(match.getAdvantage());

            handler.handle(match, player2);
            assertEquals(Points.ADVANTAGE, match.getPoints(player2));
            assertEquals(player2, match.getAdvantage());

            handler.handle(match, player1);
            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.FORTY, match.getPoints(player2));
            assertNull(match.getAdvantage());

            handler.handle(match, player1);
            assertEquals(Points.ADVANTAGE, match.getPoints(player1));

            handler.handle(match, player1);
            assertEquals(1, match.getGames(player1));
        }

        @Test
        void handle_longDeuceSequence_shouldEventuallyWin() {
            setupScore(Points.FORTY, Points.FORTY);

            for (int i = 0; i < 5; i++) {
                handler.handle(match, player1);
                assertEquals(Points.ADVANTAGE, match.getPoints(player1));

                handler.handle(match, player2);
                assertEquals(Points.FORTY, match.getPoints(player1));
                assertEquals(Points.FORTY, match.getPoints(player2));
            }

            handler.handle(match, player1);
            handler.handle(match, player1);

            assertEquals(1, match.getGames(player1));
        }
    }

    @Nested
    @DisplayName("Complete Game Scenarios")
    class CompleteGameScenarioTests {

        @Test
        void completeGame_straightWin_40to0() {
            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player1);
            handler.handle(match, player1);

            assertEquals(1, match.getGames(player1));
        }

        @Test
        void completeGame_withDeuce() {
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player1);
            handler.handle(match, player2);
            handler.handle(match, player1);
            handler.handle(match, player2);

            assertEquals(Points.FORTY, match.getPoints(player1));
            assertEquals(Points.FORTY, match.getPoints(player2));

            handler.handle(match, player1);
            assertEquals(Points.ADVANTAGE, match.getPoints(player1));

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
    }

    @Nested
    @DisplayName("Tiebreak Mode Tests")
    class TiebreakModeTests {

        @Test
        void handle_tiebreakActive_shouldNotProcessInGameHandler() {
            match.setTieBreak(true);
            Points initialPoints = match.getPoints(player1);

            handler.handle(match, player1);

            assertEquals(initialPoints, match.getPoints(player1));
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
}
