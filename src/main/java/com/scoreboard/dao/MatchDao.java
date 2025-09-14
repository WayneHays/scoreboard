package com.scoreboard.dao;

import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class MatchDao {
    private static final int PAGE_SIZE = 10;
    private static final String FIND_ALL = "FROM Match";
    private static final String FIND_BY_PLAYER = "FROM Match WHERE firstPlayer = :player OR secondPlayer = :player";
    public static final String SELECT_COUNT = "SELECT COUNT(*) ";
    private static final String PLAYER = "player";

    public void save(Match match) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.persist(match);
    }

    public List<Match> findMatchesByPage(int pageNumber) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query<Match> query = session.createQuery(FIND_ALL, Match.class);
        applyPagination(pageNumber, query);
        return query.getResultList();
    }

    public List<Match> findMatchesByPlayerByPage(Player player, int pageNumber) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query<Match> query = session.createQuery(FIND_BY_PLAYER, Match.class);
        query.setParameter(PLAYER, player);
        applyPagination(pageNumber, query);
        return query.getResultList();
    }

    public int getTotalCountOfPages() {
        return calculateTotalPages(getTotalCountOfMatches());
    }

    public int getTotalCountOfPagesByPlayer(Player player) {
        return calculateTotalPages(getTotalCountOfMatchesByPlayer(player));
    }

    private void applyPagination(int pageNumber, Query<Match> query) {
        query.setMaxResults(PAGE_SIZE);
        query.setFirstResult((pageNumber - 1) * PAGE_SIZE);
    }

    private int calculateTotalPages(int totalRecords) {
        if (totalRecords == 0) {
            return 0;
        }
        if (totalRecords < PAGE_SIZE) {
            return 1;
        }
        return (int) Math.ceil((double) totalRecords / PAGE_SIZE);
    }

    private int getTotalCountOfMatches() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(SELECT_COUNT + FIND_ALL, Long.class)
                       .getSingleResult()
                       .intValue();
    }

    private int getTotalCountOfMatchesByPlayer(Player player) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(SELECT_COUNT + FIND_BY_PLAYER, Long.class)
                       .setParameter(PLAYER, player)
                       .getSingleResult()
                       .intValue();
    }
}
