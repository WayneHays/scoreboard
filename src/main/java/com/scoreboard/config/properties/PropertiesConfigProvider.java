package com.scoreboard.config.properties;

import com.scoreboard.config.servicediscovery.ServiceProvider;
import com.scoreboard.config.context.ApplicationContext;

public class PropertiesConfigProvider implements ServiceProvider {

    @Override
    public Class<?> getServiceType() {
        return Config.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new PropertiesConfig();
    }
}
