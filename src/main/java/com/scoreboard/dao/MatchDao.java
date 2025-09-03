package com.scoreboard.dao;

import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class MatchDao {
    private static final String FIND_ALL = "FROM Match";
    private static final String FIND_BY_PLAYER = "FROM Match WHERE firstPlayer = :player OR secondPlayer = :player";
    public static final int PAGE_SIZE = 10;

    public void save(Match match) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.persist(match);
    }

    public List<Match> findByPlayer(Player player) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query<Match> query = session.createQuery(FIND_BY_PLAYER, Match.class);
        query.setParameter("player", player);
        return query.getResultList();
    }

    public List<Match> findAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query<Match> query = session.createQuery(FIND_ALL, Match.class);
        return query.getResultList();
    }

    public List<Match> findMatchesByPage(int pageNumber) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query<Match> query = session.createQuery(FIND_ALL, Match.class);
        query.setMaxResults(10);
        query.setFirstResult((pageNumber - 1) * PAGE_SIZE);

        return query.getResultList();
    }

    public List<Match> findMatchesByPlayerByPage(Player player, int pageNumber) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query<Match> query = session.createQuery(FIND_BY_PLAYER, Match.class);
        query.setParameter("player", player);
        query.setMaxResults(10);
        query.setFirstResult((pageNumber - 1) * 10);

        return query.getResultList();
    }

    public int getTotalCountOfPages() {
        return getTotalCountOfMatches() / PAGE_SIZE;
    }

    public int getTotalCountOfPagesByPlayer(Player player) {
        return getTotalCountOfMatchesByPlayer(player) / PAGE_SIZE;
    }

    private int getTotalCountOfMatches() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query<Long> query = session.createQuery("SELECT COUNT(*) " + FIND_ALL, Long.class);
        return query.getSingleResult().intValue();
    }

    private int getTotalCountOfMatchesByPlayer(Player player) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query<Long> query = session.createQuery("SELECT COUNT(*) " + FIND_BY_PLAYER, Long.class);
        query.setParameter("player", player);
        return query.getSingleResult().intValue();
    }
}
