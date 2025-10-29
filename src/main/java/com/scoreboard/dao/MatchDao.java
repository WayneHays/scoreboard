package com.scoreboard.dao;

import com.scoreboard.model.entity.Match;

import java.util.List;

public interface MatchDao {
    void save(Match match);

    List<Match> find(int pageNumber, int pageSize);

    List<Match> findByPlayerName(String name, int pageNumber, int pageSize);

    long getTotalCountOfMatches();

    long getTotalCountOfMatchesByPlayerName(String name);
}
