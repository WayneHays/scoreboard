package com.scoreboard.service.providers;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.config.ServiceProvider;
import com.scoreboard.dao.MatchDao;
import com.scoreboard.mapper.MatchesPageMapper;
import com.scoreboard.service.MatchesPageService;

public class MatchesPageServiceProvider implements ServiceProvider {

    @Override
    public Class<?> getServiceType() {
        return MatchesPageService.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new MatchesPageService(
                context.get(MatchDao.class),
                context.get(MatchesPageMapper.class));
    }
}
