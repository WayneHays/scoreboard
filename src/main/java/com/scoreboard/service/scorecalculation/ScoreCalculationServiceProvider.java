package com.scoreboard.service.scorecalculation;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.servicediscovery.ServiceProvider;
import com.scoreboard.tennisrules.TennisRules;
import com.scoreboard.tennisrules.TennisRulesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScoreCalculationServiceProvider implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(ScoreCalculationServiceProvider.class);

    @Override
    public Class<?> getServiceType() {
        return MatchService.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        TennisRules rules;

        try {
            rules = context.get(TennisRules.class);
            logger.info("Using TennisRules from context: {}",
                    rules.getClass().getSimpleName());

        } catch (IllegalStateException e) {
            logger.warn("TennisRules not found in context, using standard rules as fallback");
            logger.warn("Make sure a TennisRules provider is registered in META-INF/services");

            rules = new TennisRulesImpl();
        }

        return new MatchService(rules);
    }
}
