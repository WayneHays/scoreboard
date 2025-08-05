package com.scoreboard.service;

import com.scoreboard.dao.PlayersDao;
import com.scoreboard.exception.DaoException;
import com.scoreboard.model.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;

public class PlayerService {
    private PlayersDao playersDao;

    public PlayerService() {
        this.playersDao = new PlayersDao();
    }

    public Player create(String name) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            Player player = new Player(name);
            Optional<Player> found = playersDao.findByName(name);
            Player result = found.orElseGet(() -> playersDao.save(player));
            transaction.commit();
            return result;
        } catch (DaoException e) {
            transaction.rollback();
            throw new RuntimeException("Failed to create player", e);
        }
    }
}
