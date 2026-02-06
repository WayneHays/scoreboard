package com.scoreboard.persistence.dao;

import com.scoreboard.entity.Player;

import java.util.List;
import java.util.Set;

public class PlayerDaoImpl extends BaseDao implements PlayerDao {
    private static final String NAMES_PARAM = "names";
    private static final String FIND_BY_NAMES_HQL = "FROM Player WHERE LOWER(name) IN :names";

    public List<Player> findByNames(Set<String> names) {
        List<String> lowerNames = names.stream()
                .map(String::toLowerCase)
                .toList();

        return getCurrentSession()
                .createQuery(FIND_BY_NAMES_HQL, Player.class)
                .setParameter(NAMES_PARAM, lowerNames)
                .getResultList();
    }
}
