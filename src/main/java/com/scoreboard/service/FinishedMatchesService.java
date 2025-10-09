package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.model.entity.Match;

public class FinishedMatchesService extends BaseTransactionalService{
    private final MatchDao matchDao;

    public FinishedMatchesService(MatchDao matchDao) {
        this.matchDao = matchDao;
    }

    public void saveToDatabase(Match match) {
        executeInTransaction(
                () -> matchDao.save(match),
                "Failed to save match in database");
    }
}
