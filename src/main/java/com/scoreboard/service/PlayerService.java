package com.scoreboard.service;

import com.scoreboard.dao.PlayersDao;
import com.scoreboard.model.Player;

import java.util.Optional;

public class PlayerService extends BaseTransactionalService {
    private static final String FAILED_TO_FIND_PLAYER_MSG = "Failed to find player with name ";
    private static final String FAILED_TO_CREATE_PLAYER_MSG = "Failed to findByNameOrCreate player";

    private static final PlayerService INSTANCE = new PlayerService();
    private final PlayersDao playersDao = PlayersDao.getInstance();

    public static PlayerService getInstance() {
        return INSTANCE;
    }

    public Player findByNameOrCreate(String name) {
        return executeInTransaction(
                () -> playersDao.find(name).orElseGet(() -> playersDao.save(new Player(name))),
                FAILED_TO_CREATE_PLAYER_MSG);
    }

    public Optional<Player> find(String name) {
        return executeInTransaction(
                () -> playersDao.find(name),
                FAILED_TO_FIND_PLAYER_MSG);
    }
}
