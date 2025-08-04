package com.scoreboard.dao;

import com.scoreboard.model.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;

public class PlayersDao {

    public Player save(Player player) {
      Session session = HibernateUtil.getSessionFactory().getCurrentSession();
      session.persist(player);
      return player;
    }
}
