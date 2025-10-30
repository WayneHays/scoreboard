package com.scoreboard.config.lifecycle;

public class HibernateLifecycleComponentProvider implements LifecycleComponentProvider{

    @Override
    public LifecycleComponent createComponent() {
        return new HibernateLifecycleComponent();
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
