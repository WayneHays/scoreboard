package com.scoreboard.config;

public interface ServiceProvider {
    default Class<?> getServiceClass() {
        return this.getClass();
    }
}
