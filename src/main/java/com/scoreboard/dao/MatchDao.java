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
}
