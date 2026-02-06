package com.scoreboard.persistence.dao;

import com.scoreboard.entity.Player;

import java.util.List;
import java.util.Set;

public interface PlayerDao {
    List<Player> findByNames(Set<String> names);
}
