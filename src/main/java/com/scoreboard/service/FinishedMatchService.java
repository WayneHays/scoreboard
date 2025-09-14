package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.exception.ScoreboardServiceException;
import com.scoreboard.model.Match;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class FinishedMatchService {
    private static final String SAVE_MATCH_FAILED = "Failed to save match in database";
    private static final FinishedMatchService INSTANCE = new FinishedMatchService();
    private final MatchDao matchDao = new MatchDao();

    public static FinishedMatchService getInstance() {
        return INSTANCE;
    }

    public void saveToDatabase(Match match) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            matchDao.save(match);
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
            throw new ScoreboardServiceException(SAVE_MATCH_FAILED, e);
        }
    }
}
