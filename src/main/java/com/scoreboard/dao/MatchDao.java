package com.scoreboard.dao;

import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class MatchDao {
    private static final String FIND_LIST_BY_PLAYER = """
            FROM Match WHERE firstPlayer = :player OR secondPlayer = :player
            """;

    public Match save(Match match) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.persist(match);
        return match;
    }

    public List<Match> findByPlayer(Player player) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Query<Match> query = session.createQuery(FIND_LIST_BY_PLAYER, Match.class);
        query.setParameter("player", player);
        return query.getResultList();
    }
}
