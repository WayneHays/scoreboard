package com.scoreboard.dao;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.servicediscovery.ServiceProvider;

public class MatchDaoProvider implements ServiceProvider {

    @Override
    public Class<?> getServiceType() {
        return MatchDao.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new MatchDaoImpl();
    }
}
