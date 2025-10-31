package com.scoreboard.service.matchprocess;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.servicediscovery.ServiceProvider;
import com.scoreboard.mapper.MatchLiveViewMapper;
import com.scoreboard.mapper.MatchResultMapper;

public class MatchViewResolverProvider implements ServiceProvider {

    @Override
    public Class<MatchViewResolver> getServiceType() {
        return MatchViewResolver.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new MatchViewResolver(
                new MatchLiveViewMapper(),
                new MatchResultMapper()
        );
    }
}
