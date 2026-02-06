import com.scoreboard.persistence.dao.MatchDao;
import com.scoreboard.persistence.dao.MatchDaoImpl;
import com.scoreboard.persistence.dao.PlayerDao;
import com.scoreboard.persistence.dao.PlayerDaoImpl;
import com.scoreboard.dto.FinishedMatchDto;
import com.scoreboard.service.BaseTransactionalService;
import com.scoreboard.service.FinishedMatchPersistenceService;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FinishedMatchPersistenceTest {
    private FinishedMatchPersistenceService service;
    private Statistics statistics;

    @BeforeEach
    void setUp() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);

        PlayerDao playerDao = new PlayerDaoImpl();
        MatchDao matchDao = new MatchDaoImpl();
        BaseTransactionalService baseTransactionalService = new BaseTransactionalService();
        service = new FinishedMatchPersistenceService(matchDao, playerDao, baseTransactionalService);
    }

    @AfterEach
    void tearDown() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.createNativeMutationQuery("DELETE FROM Matches").executeUpdate();
        session.createNativeMutationQuery("DELETE FROM Players").executeUpdate();
        tx.commit();
        session.close();
    }

    @Test
    void saveFinishedMatch_withTwoNewPlayers_shouldExecute4Queries() {
        // Arrange
        FinishedMatchDto dto = new FinishedMatchDto("Novak", "Rafael", "Novak");
        statistics.clear();

        // Act
        service.saveFinishedMatch(dto);

        // Assert
        long queryCount = statistics.getPrepareStatementCount();
        assertEquals(4, queryCount,
                "Expected 4 queries: 1 SELECT + 2 INSERT players + 1 INSERT match");
    }

    @Test
    void saveFinishedMatch_withTwoExistingPlayers_shouldExecute2Queries() {
        // Arrange
        FinishedMatchDto setupDto = new FinishedMatchDto("Novak", "Rafael", "Novak");
        service.saveFinishedMatch(setupDto);

        statistics.clear();

        // Act
        FinishedMatchDto dto = new FinishedMatchDto("Novak", "Rafael", "Rafael");

        // Act
        service.saveFinishedMatch(dto);

        // Assert
        long queryCount = statistics.getPrepareStatementCount();
        assertEquals(2, queryCount,
                "Expected 2 queries: 1 SELECT (found both) + 1 INSERT match");
    }

    @Test
    void saveFinishedMatch_withOneExistingPlayer_shouldExecute3Queries() {
        // Arrange
        FinishedMatchDto setupDto = new FinishedMatchDto("Novak", "Rafael", "Novak");
        service.saveFinishedMatch(setupDto);

        statistics.clear();

        // Act
        FinishedMatchDto dto = new FinishedMatchDto("Novak", "Roger", "Novak");

        // Act
        service.saveFinishedMatch(dto);

        // Assert
        long queryCount = statistics.getPrepareStatementCount();
        assertEquals(3, queryCount,
                "Expected 3 queries: 1 SELECT + 1 INSERT new player + 1 INSERT match");
    }
}
