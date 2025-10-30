package com.scoreboard.config.lifecycle;

import com.scoreboard.config.context.ApplicationContext;

public interface LifecycleComponent {
    void start(ApplicationContext context);
    void stop();
    String getName();
}
