package com.scoreboard.service.matchespage;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.properties.Config;
import com.scoreboard.config.servicediscovery.ServiceProvider;
import com.scoreboard.dao.MatchDao;

public class MatchesPageServiceProvider implements ServiceProvider {

    @Override
    public Class<?> getServiceType() {
        return MatchesPageService.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new MatchesPageService(
                context.get(MatchDao.class),
                context.get(Config.class));
    }
}
