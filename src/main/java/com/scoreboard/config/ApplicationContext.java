package com.scoreboard.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationContext {
    private static final Map<Class<?>, Object> SERVICES;

    static {
        SERVICES = ServiceDiscovery.discoverServices();
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> type) {
        T service = (T) SERVICES.get(type);

        if (service == null) {
            throw new RuntimeException("Service not found: %s. Have you registered it in META-INF/services/?"
                    .formatted(type.getName()));
        }
        return service;
    }
}
