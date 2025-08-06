package com.scoreboard.service;

import com.scoreboard.dao.PlayersDao;
import com.scoreboard.exception.DaoException;
import com.scoreboard.exception.NotFoundException;
import com.scoreboard.exception.ScoreboardServiceException;
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

    public Player findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            Optional<Player> found = playersDao.findById(id);
            transaction.commit();
            return found.orElseThrow(() -> new NotFoundException("Player with id %d not found".formatted(id)));
        } catch (DaoException e) {
            throw new ScoreboardServiceException("Failed to find player by id: %d".formatted(id), e);
        }
    }
}
