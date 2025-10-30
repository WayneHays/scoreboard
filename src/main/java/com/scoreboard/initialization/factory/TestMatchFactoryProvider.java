package com.scoreboard.initialization.factory;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.config.servicediscovery.ServiceProvider;

public class TestMatchFactoryProvider implements ServiceProvider {

    @Override
    public Class<?> getServiceType() {
        return TestMatchFactory.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new TestMatchFactory();
    }
}
