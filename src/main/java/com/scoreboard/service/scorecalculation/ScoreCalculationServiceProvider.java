package com.scoreboard.service.scorecalculation;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.servicediscovery.ServiceProvider;

public class ScoreCalculationServiceProvider implements ServiceProvider {

    @Override
    public Class<?> getServiceType() {
        return ScoreCalculationService.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new ScoreCalculationService();
    }
}
