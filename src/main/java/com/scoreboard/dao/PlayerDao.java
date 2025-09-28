package com.scoreboard.dao;

import com.scoreboard.model.entity.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public class PlayerDao {
    private static final String FIND_BY_NAME = "FROM Player WHERE LOWER(name) = LOWER(:name)";
    private static final String FIND_BY_NAME_CONTAINING = "FROM Player WHERE LOWER(name) LIKE LOWER(:nameFragment)";

    public Player save(Player player) {
        getCurrentSession().persist(player);
        return player;
    }

    public Optional<Player> find(String name) {
        return getCurrentSession().createQuery(FIND_BY_NAME, Player.class)
                .setParameter("name", name)
                .uniqueResultOptional();
    }

    public List<Player> findByNameContaining(String nameFragment) {
        return getCurrentSession().createQuery(FIND_BY_NAME_CONTAINING, Player.class)
                .setParameter("nameFragment", "%" + nameFragment + "%")
                .getResultList();
    }

    private Session getCurrentSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }
}
