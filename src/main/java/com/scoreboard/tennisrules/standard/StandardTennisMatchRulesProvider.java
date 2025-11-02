package com.scoreboard.tennisrules.standard;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.servicediscovery.ServiceProvider;
import com.scoreboard.tennisrules.TennisMatchRules;
import com.scoreboard.tennisrules.TennisMatchRulesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardTennisMatchRulesProvider implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(StandardTennisMatchRulesProvider.class);

    @Override
    public Class<?> getServiceType() {
        return TennisMatchRules.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        logger.info("Registering STANDARD tennis match rules (TennisMatchRulesImpl with default constructor)");
        return new TennisMatchRulesImpl();
    }
}
