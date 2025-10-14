package com.scoreboard.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ApplicationContext {
    private final Map<Class<?>, Object> services;

    public ApplicationContext() {
        this.services = new ConcurrentHashMap<>();
        ServiceDiscovery.discoverAndRegister(this);
    }

    public <T> void register(Class<T> type, T instance) {
        if (services.containsKey(type)) {
            throw new IllegalStateException("Service already registered: " + type.getName());
        }

        if (!type.isInstance(instance)) {
            throw new IllegalArgumentException(
                    String.format("Type mismatch: %s is not compatible with %s",
                            instance.getClass().getName(), type.getName())
            );
        }

        services.put(type, instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        T service = (T) services.get(type);

        if (service == null) {
            throw new IllegalStateException(
                    String.format(
                            "Service not found: %s. Make sure it's registered or available via ServiceLoader",
                            type.getName()
                    )
            );
        }

        return service;
    }

    public int getServiceCount() {
        return services.size();
    }
}
