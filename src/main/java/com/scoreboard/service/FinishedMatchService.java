package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.model.entity.Match;

public class FinishedMatchService extends BaseTransactionalService{
    private final MatchDao matchDao;

    public FinishedMatchService(MatchDao matchDao) {
        this.matchDao = matchDao;
    }

    public void saveToDatabase(Match match) {
        executeInTransaction(
                () -> matchDao.save(match),
                "Failed to save match in database");
    }
}
