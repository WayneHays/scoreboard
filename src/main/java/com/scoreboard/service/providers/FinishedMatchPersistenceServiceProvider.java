package com.scoreboard.service.providers;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.config.ServiceProvider;
import com.scoreboard.dao.MatchDao;
import com.scoreboard.dao.PlayerDao;
import com.scoreboard.service.FinishedMatchPersistenceService;

public class FinishedMatchPersistenceServiceProvider implements ServiceProvider {

    @Override
    public Class<?> getServiceType() {
        return FinishedMatchPersistenceService.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new FinishedMatchPersistenceService(
                context.get(MatchDao.class),
                context.get(PlayerDao.class));
    }
}
