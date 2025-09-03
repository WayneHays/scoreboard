package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.exception.DaoException;
import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class FindMatchesService {
    private static final FindMatchesService INSTANCE = new FindMatchesService();
    private final MatchDao matchDao = new MatchDao();

    public static FindMatchesService getInstance() {
        return INSTANCE;
    }

    public List<Match> findMatchesByPlayer(Player player) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        List<Match> matches;
        try {
            matches = matchDao.findByPlayer(player);
            transaction.commit();
        } catch (DaoException e) {
            throw new RuntimeException("Failed to find matches by player", e);
        }
        return matches;
    }

    public List<Match> findAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        List<Match> matches;
        try {
            matches = matchDao.findAll();
            transaction.commit();
        } catch (DaoException e) {
            throw new RuntimeException("Failed to find all matches", e);
        }
        return matches;
    }

    public int getTotalCountOfPages() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        int totalCountOfPages;
        try {
            totalCountOfPages = matchDao.getTotalCountOfPages();
            transaction.commit();
        } catch (DaoException e) {
            throw new RuntimeException("Failed to get total count of pages", e);
        }
        return totalCountOfPages;
    }

    public int getTotalCountOfPagesByPlayer(Player player) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        int totalCountOfPages;
        try {
            totalCountOfPages = matchDao.getTotalCountOfPagesByPlayer(player);
            transaction.commit();
        } catch (DaoException e) {
            throw new RuntimeException("Failed to get total count of pages", e);
        }
        return totalCountOfPages;
    }

    public List<Match> findMatchesByPage(int pageNumber) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        List<Match> matches;
        try {
            matches = matchDao.findMatchesByPage(pageNumber);
            transaction.commit();
        } catch (DaoException e) {
            throw new RuntimeException("Failed to find matches by page", e);
        }
        return matches;
    }

    public List<Match> findMatchesByPlayerByPage(Player player, int pageNumber) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        List<Match> matches;
        try {
            matches = matchDao.findMatchesByPlayerByPage(player, pageNumber);
            transaction.commit();
        } catch (DaoException e) {
            throw new RuntimeException("Failed to find matches by player", e);
        }
        return matches;
    }
}
