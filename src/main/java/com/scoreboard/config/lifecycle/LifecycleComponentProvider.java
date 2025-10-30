package com.scoreboard.config.lifecycle;

public interface LifecycleComponentProvider {
    LifecycleComponent createComponent();
    int getOrder();
}
