package scorecalculationservice_test;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.model.OngoingMatch;
import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;
import com.scoreboard.service.ScoreCalculationService;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

public abstract class ScoreCalculationTestBase {
    protected Player player1;
    protected Player player2;
    protected Match match;
    protected OngoingMatch ongoingMatch;
    protected ScoreCalculationService service;

    @BeforeEach
    void setUp() throws Exception {
        player1 = TestPlayerFactory.createWithId("Ivan", 1L);
        player2 = TestPlayerFactory.createWithId("Petr", 2L);

        Score score = new Score(player1, player2);
        match = new Match(player1, player2);
        ongoingMatch = new OngoingMatch(match, score, UUID.randomUUID());
        service = ApplicationContext.get(ScoreCalculationService.class);
    }
}
