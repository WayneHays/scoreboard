package scorecalculationservice_test;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.model.OngoingMatch;
import com.scoreboard.model.entity.Player;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.service.ScoreCalculationService;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

public abstract class ScoreCalculationTestBase {
    protected Player player1;
    protected Player player2;
    protected OngoingMatch ongoingMatch;
    protected OngoingMatchesService ongoingMatchesService;
    protected ScoreCalculationService scoreCalculationService;

    @BeforeEach
    void setUp() {
        player1 = new Player("Ivan");
        player2 = new Player("Petr");
        ongoingMatchesService = ApplicationContext.get(OngoingMatchesService.class);
        scoreCalculationService = ApplicationContext.get(ScoreCalculationService.class);
        UUID uuid = ongoingMatchesService.createMatch(player1, player2);
        ongoingMatch = ongoingMatchesService.get(uuid);
    }
}
