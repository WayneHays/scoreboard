package com.scoreboard.service.scorecalculation;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.servicediscovery.ServiceProvider;
import com.scoreboard.service.scorecalculation.rules.TennisMatchRules;
import com.scoreboard.service.scorecalculation.rules.TennisMatchRulesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScoreCalculationServiceProvider implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(ScoreCalculationServiceProvider.class);

    @Override
    public Class<?> getServiceType() {
        return ScoreCalculationService.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        TennisMatchRules rules;

        try {
            rules = context.get(TennisMatchRules.class);
            logger.info("Using TennisMatchRules from context: {}",
                    rules.getClass().getSimpleName());

        } catch (IllegalStateException e) {
            logger.warn("TennisMatchRules not found in context, using standard rules as fallback");
            logger.warn("Make sure a TennisMatchRules provider is registered in META-INF/services");

            rules = new TennisMatchRulesImpl();
        }

        return new ScoreCalculationService(rules);
    }
}
