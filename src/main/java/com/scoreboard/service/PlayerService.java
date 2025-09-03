package com.scoreboard.service;

import com.scoreboard.dao.PlayersDao;
import com.scoreboard.exception.DaoException;
import com.scoreboard.model.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;

public class PlayerService {
    private static final PlayerService INSTANCE = new PlayerService();
    private final PlayersDao playersDao;

    public PlayerService() {
        this.playersDao = new PlayersDao();
    }

    public static PlayerService getInstance() {
        return INSTANCE;
    }

    public Player create(String name) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            Optional<Player> found = playersDao.findByName(name);
            Player result = found.orElseGet(() -> playersDao.save(new Player(name)));
            transaction.commit();
            return result;
        } catch (DaoException e) {
            transaction.rollback();
            throw new RuntimeException("Failed to create player", e);
        }
    }

    public Optional<Player> find(String name) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            return playersDao.findByName(name);
        } catch (DaoException e) {
            transaction.rollback();
            throw new RuntimeException("Failed to find player", e);
        }
    }
}
