package com.scoreboard.config.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ApplicationContext {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationContext.class.getName());
    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    public <T> void register(Class<T> type, T instance) {
        validateRegistration(type, instance);
        services.put(type, instance);
        logger.debug("Registered service: {}", type.getSimpleName());
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

    private <T> void validateRegistration(Class<T> type, T instance) {
        if (services.containsKey(type)) {
            throw new IllegalStateException("Service already registered: " + type.getName());
        }

        if (!type.isInstance(instance)) {
            throw new IllegalArgumentException(
                    String.format("Type mismatch: %s is not compatible with %s",
                            instance.getClass().getName(), type.getName())
            );
        }
    }

    public int getServiceCount() {
        return services.size();
    }
}
