package com.scoreboard.service;

import com.scoreboard.dao.PlayersDao;
import com.scoreboard.model.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PlayerService {
    public PlayersDao playersDao;

    public PlayerService() {
        this.playersDao = new PlayersDao();
    }

    public void create(String name) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            Player player = new Player(name);
            Player found = playersDao.findByName(player);

            if (found == null) {
                playersDao.save(player);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException("Failed to create player", e);
        }
    }
}
