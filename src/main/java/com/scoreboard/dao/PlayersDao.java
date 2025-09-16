package com.scoreboard.dao;

import com.scoreboard.model.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;

import java.util.Optional;

public class PlayersDao {
    private static final String FIND_BY_NAME_SQL = "FROM Player WHERE name = :name";
    private static final String NAME = "name";

    private static final PlayersDao INSTANCE = new PlayersDao();

    public static PlayersDao getInstance() {
        return INSTANCE;
    }

    private PlayersDao() {}

    public Player save(Player player) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.persist(player);
        return player;
    }

    public Optional<Player> find(String name) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        return session.createQuery(FIND_BY_NAME_SQL, Player.class)
                .setParameter(NAME, name)
                .uniqueResultOptional();
    }
}
