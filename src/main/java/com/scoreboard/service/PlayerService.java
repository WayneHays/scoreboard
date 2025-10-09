package com.scoreboard.service;

import com.scoreboard.dao.PlayerDao;
import com.scoreboard.model.entity.Player;

import java.util.Optional;

public class PlayerService extends BaseTransactionalService {
    private final PlayerDao playerDao;

    public PlayerService(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }

    public Player create(String name) {
        Player player = new Player(name);
        executeInTransaction(
                () -> playerDao.save(player),
                "Failed to create player with name: " + name);
        return player;
    }

    public Optional<Player> find(String name) {
        return executeInTransaction(
                () -> playerDao.find(name),
                "Failed to get player with name ");
    }
}
