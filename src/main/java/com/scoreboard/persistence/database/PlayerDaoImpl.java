package com.scoreboard.persistence.database;

import com.scoreboard.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PlayerDaoImpl extends BaseDao implements PlayerDao {
    private static final String NAME_PARAM = "name";
    private static final String FIND_BY_NAME_HQL = """
        FROM Player p
        WHERE p.name ILIKE :name
        """;

    public Player save(Player player) {
        getCurrentSession().persist(player);
        return player;
    }

    public Optional<Player> find(String name) {
        return getCurrentSession()
                .createQuery(FIND_BY_NAME_HQL, Player.class)
                .setParameter(NAME_PARAM, name.toLowerCase())
                .uniqueResultOptional();
    }

    public List<Player> findByNames(Set<String> names) {
        List<String> lowerNames = names.stream()
                .map(String::toLowerCase)
                .toList();
        return getCurrentSession()
                .createQuery("FROM Player WHERE name IN :names", Player.class)
                .setParameter("names", lowerNames)
                .getResultList();
    }
}
