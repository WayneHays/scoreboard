package com.scoreboard.service;

import com.scoreboard.dao.PlayersDao;
import com.scoreboard.model.Player;

import java.util.Optional;

public class PlayerService extends BaseTransactionalService {
    private static final PlayerService INSTANCE = new PlayerService();
    private final PlayersDao playersDao = PlayersDao.getInstance();

    public static PlayerService getInstance() {
        return INSTANCE;
    }


    public Player create(String name) {
        return executeInTransaction(
                () -> playersDao.save(new Player(name)),
                "Failed to create player with name: " + name);
    }

    public Optional<Player> find(String name) {
        return executeInTransaction(
                () -> playersDao.find(name),
                "Failed to get player with name ");
    }
}
