package scorecalculationservice_test;

import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;
import com.scoreboard.service.ScoreCalculationService;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

public abstract class ScoreCalculationTestBase {
    protected Player player1;
    protected Player player2;
    protected Score score;
    protected MatchWithScore matchWithScore;
    protected ScoreCalculationService service;

    @BeforeEach
    void setUp() {
        player1 = new Player("Ivan");
        player2 = new Player("Petr");
        score = createInitialScore(player1, player2);
        matchWithScore = new MatchWithScore(createMatch(player1, player2), score);
        service = ScoreCalculationService.getInstance();
    }

    protected Score createInitialScore(Player player1, Player player2) {
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
                tieBreakPoints
                );
    }

    protected Match createMatch(Player player1, Player player2) {
        return new Match(player1, player2);
    }
}
