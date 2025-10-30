package com.scoreboard.config.servicediscovery;

import com.scoreboard.config.context.ApplicationContext;

public interface ServiceProvider {
    Class<?> getServiceType();
    Object createService(ApplicationContext context);
}
