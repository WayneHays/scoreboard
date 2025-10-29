package com.scoreboard.dao;

import com.scoreboard.model.entity.Player;

import java.util.Optional;

public interface PlayerDao {
    void save(Player player);

    Optional<Player> find(String name);
}
