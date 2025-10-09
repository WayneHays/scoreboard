package com.scoreboard.dao;

import com.scoreboard.model.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;

import java.util.Optional;

public class PlayerDao {
    private static final String FIND_BY_NAME = "FROM Player WHERE LOWER(name) = LOWER(:name)";

    public void save(Player player) {
        getCurrentSession().persist(player);
    }

    public Optional<Player> find(String name) {
        return getCurrentSession().createQuery(FIND_BY_NAME, Player.class)
                .setParameter("name", name)
                .uniqueResultOptional();
    }

    private Session getCurrentSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }
}
