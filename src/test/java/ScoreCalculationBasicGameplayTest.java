import com.scoreboard.model.Match;
import com.scoreboard.model.MatchWithScore;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;
import com.scoreboard.service.ScoreCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестируется:
 * - ранний выход из метода при статусе матча "завершен";
 * - набор очков игроком;
 * - победа в гейме (без deuce)
 */

class ScoreCalculationServiceBasicGameplayTest {
    private Player player1;
    private Player player2;
    private Score score;
    private MatchWithScore matchWithScore;
    private ScoreCalculationService service;

    @BeforeEach
    void setUp() {
        player1 = new Player("Ivan");
        player2 = new Player("Petr");
        score = createInitialScore(player1, player2);
        matchWithScore = new MatchWithScore(createMatch(player1, player2), score);
        service = ScoreCalculationService.getInstance();
    }

    @Test
    void shouldReturnIfMatchFinished() {
        score.setMatchFinished(true);

        int initialPlayer1Points = score.getPoints(player1);
        int initialPlayer1Games = score.getGames(player1);
        int initialPlayer1Sets = score.getSets(player1);
        int initialPlayer2Points = score.getPoints(player2);
        int initialPlayer2Games = score.getGames(player2);
        int initialPlayer2Sets = score.getSets(player2);

        Score result = service.calculate(matchWithScore, player1);

        assertEquals(initialPlayer1Points, score.getPoints(player1));
        assertEquals(initialPlayer1Games, score.getGames(player1));
        assertEquals(initialPlayer1Sets, score.getSets(player1));
        assertEquals(initialPlayer2Points, score.getPoints(player2));
        assertEquals(initialPlayer2Games, score.getGames(player2));
        assertEquals(initialPlayer2Sets, score.getSets(player2));

        assertSame(score, result);
        assertTrue(score.isMatchFinished());
    }

    @Test
    void testWinPointProgression() {
        service.calculate(matchWithScore, player1);
        assertEquals(15, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));

        service.calculate(matchWithScore, player1);
        assertEquals(30, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));

        service.calculate(matchWithScore, player1);
        assertEquals(40, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));

        service.calculate(matchWithScore, player1);
        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
    }

    @Test
    void testCompleteGameWin() {
        service.calculate(matchWithScore, player1);
        service.calculate(matchWithScore, player1);
        service.calculate(matchWithScore, player1);
        service.calculate(matchWithScore, player1);

        assertEquals(0, score.getPoints(player1));
        assertEquals(0, score.getPoints(player2));
        assertEquals(1, score.getGames(player1));
        assertEquals(0, score.getGames(player2));
    }

    private Score createInitialScore(Player player1, Player player2) {
        Map<Player, Integer> points = new HashMap<>();
        Map<Player, Integer> games = new HashMap<>();
        Map<Player, Integer> sets = new HashMap<>();
        Map<Player, Integer> tieBreakPoints = new HashMap<>();

        points.put(player1, 0);
        points.put(player2, 0);
        games.put(player1, 0);
        games.put(player2, 0);
        sets.put(player1, 0);
        sets.put(player2, 0);
        tieBreakPoints.put(player1, 0);
        tieBreakPoints.put(player2, 0);

        return new Score(
                points,
                games,
                sets,
                tieBreakPoints,
                null,
                false,
                false,
                false);
    }

    private Match createMatch(Player player1, Player player2) {
        return new Match(player1, player2);
    }
}
