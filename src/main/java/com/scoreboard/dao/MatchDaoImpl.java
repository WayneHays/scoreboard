package com.scoreboard.dao;

import com.scoreboard.model.entity.Match;

import java.util.List;

public class MatchDaoImpl extends BaseHibernateDao<Match> implements MatchDao{
    private static final String FIND_ALL = "FROM Match ORDER BY id DESC";
    private static final String FIND_BY_PLAYER_NAME =
            "SELECT DISTINCT m FROM Match m " +
            "WHERE LOWER(m.firstPlayer.name) LIKE LOWER(:namePattern) " +
            "   OR LOWER(m.secondPlayer.name) LIKE LOWER(:namePattern) " +
            "ORDER BY m.id DESC";

    private static final String COUNT_ALL = "SELECT COUNT(m) FROM Match m";
    private static final String COUNT_BY_PLAYER_NAME =
            "SELECT COUNT(DISTINCT m) FROM Match m " +
            "WHERE LOWER(m.firstPlayer.name) LIKE LOWER(:namePattern) " +
            "   OR LOWER(m.secondPlayer.name) LIKE LOWER(:namePattern)";
    private static final String NAME_PATTERN_PARAM = "namePattern";

    public List<Match> find(int pageNumber, int pageSize) {
        return createPaginatedQuery(FIND_ALL, Match.class, pageNumber, pageSize)
                .getResultList();
    }

    public List<Match> findByPlayerName(String name, int pageNumber, int pageSize) {
        return createPaginatedQuery(FIND_BY_PLAYER_NAME, Match.class, pageNumber, pageSize)
                .setParameter(NAME_PATTERN_PARAM, "%" + name + "%")
                .getResultList();
    }

    public long getTotalCountOfMatches() {
        return getCurrentSession()
                .createQuery(COUNT_ALL, Long.class)
                .getSingleResult();
    }

    public long getTotalCountOfMatchesByPlayerName(String name) {
        return getCurrentSession()
                .createQuery(COUNT_BY_PLAYER_NAME, Long.class)
                .setParameter(NAME_PATTERN_PARAM, "%" + name + "%")
                .getSingleResult();
    }
}
