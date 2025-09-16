package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.model.Match;

public class FinishedMatchService extends BaseTransactionalService{
    private static final String SAVE_MATCH_FAILED = "Failed to save match in database";

    private static final FinishedMatchService INSTANCE = new FinishedMatchService();
    private final MatchDao matchDao = MatchDao.getInstance();

    public static FinishedMatchService getInstance() {
        return INSTANCE;
    }

    public void saveToDatabase(Match match) {
        executeInTransaction(() -> matchDao.save(match),
                SAVE_MATCH_FAILED);
    }
}
