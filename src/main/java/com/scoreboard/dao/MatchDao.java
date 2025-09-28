package com.scoreboard.dao;

import com.scoreboard.config.ApplicationConfig;
import com.scoreboard.model.entity.Match;
import com.scoreboard.model.entity.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class MatchDao {
    private static final String FIND_ALL = "FROM Match ORDER BY id DESC";
    private static final String FIND_BY_PLAYER = "FROM Match WHERE firstPlayer = :player OR secondPlayer = :player ORDER BY id DESC";
    private static final String COUNT_ALL = "SELECT COUNT(id) FROM Match";
    private static final String COUNT_BY_PLAYER = "SELECT COUNT(id) FROM Match WHERE firstPlayer = :player OR secondPlayer = :player";
    private static final String PLAYER_PARAM = "player";

    public void save(Match match) {
        getCurrentSession().persist(match);
    }

    public List<Match> find(int pageNumber) {
        return createPaginatedQuery(FIND_ALL, pageNumber).getResultList();
    }

    public List<Match> find(Player player, int pageNumber) {
        Query<Match> query = createPaginatedQuery(FIND_BY_PLAYER, pageNumber);
        query.setParameter(PLAYER_PARAM, player);
        return query.getResultList();
    }

    public List<Match> findAllByPlayer(Player player) {
        return getCurrentSession().createQuery(FIND_BY_PLAYER, Match.class)
                .setParameter(PLAYER_PARAM, player)
                .getResultList();
    }

    public int getTotalCountOfMatches() {
        return getCurrentSession().createQuery(COUNT_ALL, Long.class)
                .getSingleResult()
                .intValue();
    }

    public int getTotalCountOfMatchesByPlayer(Player player) {
        return getCurrentSession().createQuery(COUNT_BY_PLAYER, Long.class)
                .setParameter(PLAYER_PARAM, player)
                .getSingleResult()
                .intValue();
    }

    private Session getCurrentSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    private Query<Match> createPaginatedQuery(String hql, int pageNumber) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Page number must be positive");
        }

        Query<Match> query = getCurrentSession().createQuery(hql, Match.class);
        query.setMaxResults(ApplicationConfig.PAGE_SIZE);
        query.setFirstResult((pageNumber - 1) * ApplicationConfig.PAGE_SIZE);
        return query;
    }
}
