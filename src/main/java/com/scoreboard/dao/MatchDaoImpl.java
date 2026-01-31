package com.scoreboard.dao;

import com.scoreboard.model.entity.Match;

import java.util.List;

public class MatchDaoImpl extends BaseDao<Match> implements MatchDao {
    private static final String NAME_PATTERN_PARAM = "namePattern";

    private static final String FROM_MATCH_HQL = "FROM Match m ";
    private static final String FILTER_BY_PLAYER_NAME_HQL = """
            WHERE m.firstPlayer.name ILIKE :namePattern
            OR m.secondPlayer.name ILIKE :namePattern
            """;
    private static final String JOIN_FETCH_HQL = """
            LEFT JOIN FETCH m.firstPlayer
            LEFT JOIN FETCH m.secondPlayer
            LEFT JOIN FETCH m.winner
            """;
    private static final String FIND_ALL_HQL = FROM_MATCH_HQL + JOIN_FETCH_HQL;
    private static final String FIND_BY_NAME_HQL = FIND_ALL_HQL + JOIN_FETCH_HQL + FILTER_BY_PLAYER_NAME_HQL;
    private static final String COUNT_ALL_HQL = "SELECT COUNT(m) " + FROM_MATCH_HQL;
    private static final String COUNT_BY_PLAYER_NAME_HQL = COUNT_ALL_HQL + FILTER_BY_PLAYER_NAME_HQL;

    public List<Match> find(int offset, int pageSize) {
        return createPaginatedQuery(FIND_ALL_HQL, Match.class, offset, pageSize)
                .getResultList();
    }

    public List<Match> findByPlayerName(String name, int offset, int pageSize) {
        return createPaginatedQuery(FIND_BY_NAME_HQL, Match.class, offset, pageSize)
                .setParameter(NAME_PATTERN_PARAM, "%" + name + "%")
                .getResultList();
    }

    public long countTotal() {
        return getCurrentSession()
                .createQuery(COUNT_ALL_HQL, Long.class)
                .getSingleResult();
    }

    public long countWithPlayer(String name) {
        return getCurrentSession()
                .createQuery(COUNT_BY_PLAYER_NAME_HQL, Long.class)
                .setParameter(NAME_PATTERN_PARAM, "%" + name + "%")
                .getSingleResult();
    }
}
