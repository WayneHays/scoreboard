package com.scoreboard.service;

import com.scoreboard.dao.PlayersDao;
import com.scoreboard.exception.ScoreboardServiceException;
import com.scoreboard.model.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;

public class PlayerService {
    private static final String FAILED_TO_FIND_PLAYER_MSG = "Failed to find player with name ";
    private static final String FAILED_TO_CREATE_PLAYER_MSG = "Failed to create player";

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
            Optional<Player> result = playersDao.findByName(name);
            Player player = result.orElseGet(() -> playersDao.save(new Player(name)));
            transaction.commit();
            return player;
        } catch (RuntimeException e) {
            transaction.rollback();
            throw new ScoreboardServiceException(FAILED_TO_CREATE_PLAYER_MSG, e);
        }
    }

    public Optional<Player> find(String name) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            Optional<Player> result = playersDao.findByName(name);
            transaction.commit();
            return result;
        }  catch (RuntimeException e) {
            transaction.rollback();
            throw new ScoreboardServiceException(FAILED_TO_FIND_PLAYER_MSG + name, e);
        }
    }
}
