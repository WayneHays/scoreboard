package com.scoreboard.dao;

import com.scoreboard.exception.DaoException;
import com.scoreboard.model.Player;
import com.scoreboard.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class PlayersDao {
    private static final String FIND_BY_NAME_SQL = "FROM Player WHERE name = :name";

    public Player save(Player player) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.persist(player);
            return player;
        } catch (Exception e) {
            throw new DaoException("Failed to create player", e);
        }
    }

    public Optional<Player> findByName(String name) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            Query<Player> query = session.createQuery(FIND_BY_NAME_SQL);
            query.setParameter("name", name);
            List<Player> resultList = query.getResultList();

            if (resultList.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(resultList.getFirst());
        } catch (Exception e) {
            throw new DaoException("Failed to find player", e);
        }
    }

    public Optional<Player> findById(Long id) {
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            Player player = session.find(Player.class, id);

            return Optional.ofNullable(player);
        } catch (Exception e) {
            throw new DaoException("Failed to find player by id", e);
        }
    }
}
