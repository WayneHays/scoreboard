package com.scoreboard.dao;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.servicediscovery.ServiceProvider;

public class PlayerDaoProvider implements ServiceProvider {

    @Override
    public Class<?> getServiceType() {
        return PlayerDao.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new PlayerDaoImpl();
    }
}
