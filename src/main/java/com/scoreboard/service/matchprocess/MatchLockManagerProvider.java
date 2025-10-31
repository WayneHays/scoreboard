package com.scoreboard.service.matchprocess;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.servicediscovery.ServiceProvider;

public class MatchLockManagerProvider implements ServiceProvider {

    @Override
    public Class<?> getServiceType() {
        return MatchLockManager.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new MatchLockManager();
    }
}
