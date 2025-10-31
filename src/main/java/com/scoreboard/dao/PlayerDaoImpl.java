package com.scoreboard.dao;

import com.scoreboard.model.entity.Player;

import java.util.Optional;

public class PlayerDaoImpl extends BaseHibernateDao<Player> implements PlayerDao {
    private static final String FIND_BY_NAME = "FROM Player WHERE LOWER(name) = LOWER(:name)";

    public Optional<Player> find(String name) {
        return getCurrentSession()
                .createQuery(FIND_BY_NAME, Player.class)
                .setParameter("name", name)
                .uniqueResultOptional();
    }
}
