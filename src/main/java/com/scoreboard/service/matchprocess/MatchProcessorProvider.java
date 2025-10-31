package com.scoreboard.service.matchprocess;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.servicediscovery.ServiceProvider;
import com.scoreboard.service.finishedmatchpersistence.FinishedMatchPersistenceService;
import com.scoreboard.service.ongoingmatches.OngoingMatchesService;
import com.scoreboard.service.scorecalculation.ScoreCalculationService;

public class MatchProcessorProvider implements ServiceProvider {

    @Override
    public Class<MatchProcessor> getServiceType() {
        return MatchProcessor.class;
    }

    @Override
    public MatchProcessor createService(ApplicationContext context) {
        return new MatchProcessor(
                context.get(OngoingMatchesService.class),
                context.get(ScoreCalculationService.class),
                context.get(FinishedMatchPersistenceService.class),
                context.get(MatchLockManager.class)
        );
    }
}
