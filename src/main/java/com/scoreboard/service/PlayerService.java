package com.scoreboard.service;

import com.scoreboard.dao.PlayerDao;
import com.scoreboard.model.entity.Player;

import java.util.List;
import java.util.Optional;

public class PlayerService extends BaseTransactionalService {
    private final PlayerDao playerDao;

    public PlayerService(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }

    public Player create(String name) {
        return executeInTransaction(
                () -> playerDao.save(new Player(name)),
                "Failed to create player with name: " + name);
    }

    public Optional<Player> find(String name) {
        return executeInTransaction(
                () -> playerDao.find(name),
                "Failed to get player with name ");
    }

    public List<Player> findByNameContaining(String nameFragment) {
        return executeInTransaction(
                () -> playerDao.findByNameContaining(nameFragment),
                "Failed to search players by name fragment: " + nameFragment);
    }
}
