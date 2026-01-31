package com.scoreboard.dao;

import com.scoreboard.model.entity.Player;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class PlayerDaoImpl extends BaseDao<Player> implements PlayerDao {
    private static final String NAME_PARAM = "name";
    private static final String FIND_BY_NAME_HQL = """
        FROM Player p
        WHERE p.name ILIKE :name
        """;

    public Optional<Player> find(String name) {
        return getCurrentSession()
                .createQuery(FIND_BY_NAME_HQL, Player.class)
                .setParameter(NAME_PARAM, name.toLowerCase())
                .uniqueResultOptional();
    }
}
