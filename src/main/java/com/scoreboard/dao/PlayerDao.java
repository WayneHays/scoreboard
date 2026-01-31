package com.scoreboard.dao;

import com.scoreboard.model.entity.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerDao {
    Player save(Player player);

    Optional<Player> find(String name);
}
