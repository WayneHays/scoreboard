package com.scoreboard.tennisrules.standard;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.servicediscovery.ServiceProvider;
import com.scoreboard.tennisrules.TennisRules;
import com.scoreboard.tennisrules.TennisRulesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardTennisMatchRulesProvider implements ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(StandardTennisMatchRulesProvider.class);

    @Override
    public Class<?> getServiceType() {
        return TennisRules.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        logger.info("Registering STANDARD tennis match rules (TennisMatchRulesImpl with default constructor)");
        return new TennisRulesImpl();
    }
}
