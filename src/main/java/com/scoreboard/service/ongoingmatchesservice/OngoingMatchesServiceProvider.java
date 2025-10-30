package com.scoreboard.service.ongoingmatchesservice;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.servicediscovery.ServiceProvider;

public class OngoingMatchesServiceProvider implements ServiceProvider {

    @Override
    public Class<?> getServiceType() {
        return OngoingMatchesService.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new OngoingMatchesService();
    }
}
