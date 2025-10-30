package com.scoreboard.config.lifecycle;

public class DatabaseInitializerLifecycleComponentProvider implements LifecycleComponentProvider{

    @Override
    public LifecycleComponent createComponent() {
        return new DatabaseInitializerLifecycleComponent();
    }

    @Override
    public int getOrder() {
        return 200;
    }
}
