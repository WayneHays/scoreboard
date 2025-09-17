package scorecalculationservice_test;

import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;
import com.scoreboard.service.ScoreCalculationService;
import org.junit.jupiter.api.BeforeEach;

public abstract class ScoreCalculationTestBase {
    protected Player player1;
    protected Player player2;
    protected Score score;
    protected Match match;
    protected ScoreCalculationService service;

    @BeforeEach
    void setUp() {
        player1 = new Player("Ivan");
        player2 = new Player("Petr");
        score = new Score(player1, player2);
        match = new Match(player1, player2);
        service = ScoreCalculationService.getInstance();
    }
}
