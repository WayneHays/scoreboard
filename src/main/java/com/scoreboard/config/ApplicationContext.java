package com.scoreboard.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ApplicationContext {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationContext.class.getName());
    private final Map<Class<?>, Object> services;

    public ApplicationContext() {
        this.services = new ConcurrentHashMap<>();
        logger.info("Initializing ApplicationContext");

        ServiceDiscovery.discoverAndRegister(this);
        logger.info("ApplicationContext initialized with {} services",services.size());
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

        if (logger.isDebugEnabled()) {
            logger.debug("Registered service: {}", type.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        T service = (T) services.get(type);

        if (service == null) {
            logger.warn("Service not found: {}", type.getName());
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
        int count = services.size();

        if (logger.isTraceEnabled()) {
            logger.trace("Current service count: {}", count);
        }
        return count;
    }
}
