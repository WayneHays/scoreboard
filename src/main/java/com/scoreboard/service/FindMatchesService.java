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
    private static final String FAILED_TO_GET_TOTAL_PAGES = "Failed to get total count of pages";
    private static final String FAILED_TO_GET_TOTAL_PAGES_BY_PLAYER = "Failed to get total count of pages by player";
    private static final String FAILED_TO_FIND_MATCHES_BY_PAGE = "Failed to find matches by page";

    private static final FindMatchesService INSTANCE = new FindMatchesService();
    private final MatchDao matchDao = new MatchDao();

    public static FindMatchesService getInstance() {
        return INSTANCE;
    }

    public int getTotalCountOfPages() {
        return executeInTransaction(
                matchDao::getTotalCountOfPages,
                FAILED_TO_GET_TOTAL_PAGES
        );
    }

    public int getTotalCountOfPagesByPlayer(Player player) {
        return executeInTransaction(
                () -> matchDao.getTotalCountOfPagesByPlayer(player),
                FAILED_TO_GET_TOTAL_PAGES_BY_PLAYER
        );
    }

    public List<Match> findMatchesByPage(int pageNumber) {
        return executeInTransaction(
                () -> matchDao.findMatchesByPage(pageNumber),
                FAILED_TO_FIND_MATCHES_BY_PAGE
        );
    }

    public List<Match> findMatchesByPlayerByPage(Player player, int pageNumber) {
        return executeInTransaction(
                () -> matchDao.findMatchesByPlayerByPage(player, pageNumber),
                FAILED_TO_FIND_MATCHES_BY_PAGE
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
