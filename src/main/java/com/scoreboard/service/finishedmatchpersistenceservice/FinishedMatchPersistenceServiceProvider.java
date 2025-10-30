package com.scoreboard.service.finishedmatchpersistenceservice;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.servicediscovery.ServiceProvider;
import com.scoreboard.dao.MatchDao;
import com.scoreboard.dao.PlayerDao;

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
