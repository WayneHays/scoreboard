package com.scoreboard.persistence.database;

import com.scoreboard.entity.Match;

import java.util.List;

public interface MatchDao {
    void save(Match match);

    List<Match> find(int offset, int pageSize);

    List<Match> findByPlayerName(String name, int offset, int pageSize);

    long countTotal();

    long countWithPlayer(String name);
}
