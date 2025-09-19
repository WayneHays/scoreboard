package com.scoreboard.dao;

import com.scoreboard.config.AppConfig;
import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
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

    private static final MatchDao INSTANCE = new MatchDao();

    public static MatchDao getInstance() {
        return INSTANCE;
    }

    private MatchDao() {
    }

    public void save(Match match) {
        getCurrentSession().persist(match);
    }

    public List<Match> find(int pageNumber) {
        Query<Match> query = getCurrentSession().createQuery(FIND_ALL, Match.class);
        applyPagination(pageNumber, query);
        return query.getResultList();
    }

    public List<Match> find(Player player, int pageNumber) {
        Query<Match> query = getCurrentSession().createQuery(FIND_BY_PLAYER, Match.class);
        query.setParameter(PLAYER_PARAM, player);
        applyPagination(pageNumber, query);
        return query.getResultList();
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

    private void applyPagination(int pageNumber, Query<Match> query) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Page number must be positive");
        }
        query.setMaxResults(AppConfig.PAGE_SIZE);
        query.setFirstResult((pageNumber - 1) * AppConfig.PAGE_SIZE);
    }
}
