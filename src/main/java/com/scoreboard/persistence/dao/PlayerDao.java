package com.scoreboard.persistence.dao;

import com.scoreboard.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PlayerDao {
    Player save(Player player);

    Optional<Player> find(String name);

    List<Player> findByNames(Set<String> names);
}
