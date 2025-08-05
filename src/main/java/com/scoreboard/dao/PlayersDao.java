package com.scoreboard.dao;

import com.scoreboard.exception.DaoException;
import com.scoreboard.model.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;

public class PlayersDao {

    public Player save(Player player) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.persist(player);
            return player;
        } catch (Exception e) {
            throw new DaoException("Failed to create player", e);
        }
    }

    public Player findByName(Player player) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            return session.find(Player.class, player);
        } catch (Exception e) {
            throw new DaoException("Failed to find player", e);
        }
    }
}
