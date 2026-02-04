package com.scoreboard.context;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ApplicationContext {
    private static final String NOT_FOUND_MESSAGE = "Service not found: %s. Make sure it's registered in ApplicationContext";
    private static final String ALREADY_REGISTERED_MESSAGE = "Service already registered: ";

    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    public <T> void register(Class<T> type, T instance) {
        validate(type);
        services.put(type, instance);
        log.debug("Registered service: {}", type.getSimpleName());
    }

    public <T> T get(Class<T> type) {
        T service = type.cast(services.get(type));

        if (service == null) {
            throw new IllegalStateException(
                    String.format(
                            NOT_FOUND_MESSAGE,
                            type.getName()
                    )
            );
        }

        return service;
    }

    private <T> void validate(Class<T> type) {
        if (services.containsKey(type)) {
            throw new IllegalStateException(ALREADY_REGISTERED_MESSAGE + type.getName());
        }
    }
}
