package com.scoreboard.dao;

import com.scoreboard.model.entity.Match;

import java.util.List;

public interface MatchDao {
    void save(Match match);

    List<Match> find(int offset, int pageSize);

    List<Match> findByPlayerName(String name, int offset, int pageSize);

    int countTotal();

    int countWithPlayer(String name);
}
