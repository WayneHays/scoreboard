package com.scoreboard.config;

public class PropertiesConfigProvider implements ServiceProvider{

    @Override
    public Class<?> getServiceType() {
        return Config.class;
    }

    @Override
    public Object createService(ApplicationContext context) {
        return new PropertiesConfig();
    }
}
