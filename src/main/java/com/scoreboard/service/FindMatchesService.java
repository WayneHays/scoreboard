package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.exception.ScoreboardServiceException;
import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.function.Supplier;

public class FindMatchesService {
    private static final FindMatchesService INSTANCE = new FindMatchesService();
    private final MatchDao matchDao = new MatchDao();

    public static FindMatchesService getInstance() {
        return INSTANCE;
    }

    public int getTotalCountOfPages() {
        return executeInTransaction(
                matchDao::getTotalCountOfPages,
                "Failed to get total count of pages"
        );
    }

    public int getTotalCountOfPagesByPlayer(Player player) {
        return executeInTransaction(
                () -> matchDao.getTotalCountOfPagesByPlayer(player),
                "Failed to get total count of pages by player"
        );
    }

    public List<Match> findMatchesByPage(int pageNumber) {
        return executeInTransaction(
                () -> matchDao.findMatchesByPage(pageNumber),
                "Failed to find matches by page"
        );
    }

    public List<Match> findMatchesByPlayerByPage(Player player, int pageNumber) {
        return executeInTransaction(
                () -> matchDao.findMatchesByPlayerByPage(player, pageNumber),
                "Failed to find matches by page"
        );
    }

    private <T> T executeInTransaction(Supplier<T> operation, String errorMessage) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            T result = operation.get();
            transaction.commit();
            return result;
        } catch (RuntimeException e) {
            transaction.rollback();
            throw new ScoreboardServiceException(errorMessage, e);
        }
    }
}
