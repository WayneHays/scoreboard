package com.scoreboard.config;

public interface ServiceProvider {

    default Class<?> getServiceType() {
        return this.getClass();
    }

    default Object createService(ApplicationContext context) {
        try {
            return this.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to create service instance for " + this.getClass().getName() +
                    ". Override createService() method or ensure no-arg constructor exists.", e
            );
        }
    }
}
