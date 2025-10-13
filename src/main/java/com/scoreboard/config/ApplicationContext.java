package com.scoreboard.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationContext {
    private static final Map<Class<?>, Object> SERVICES = new ConcurrentHashMap<>();

    static {
        initialize();
    }

    private static void initialize() {
        try {
            ServiceLoader<ServiceProvider> loader = ServiceLoader.load(ServiceProvider.class);

            for (ServiceProvider provider : loader) {
                Class<?> clazz = provider.getServiceClass();
                register(clazz, provider);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize ApplicationContext", e);
        }
    }

    private static void register(Class<?> clazz, Object instance) {
        if (SERVICES.containsKey(clazz)) {
            throw new RuntimeException("Service already registered: " + clazz.getName());
        }

        if (!clazz.isInstance(instance)) {
            throw new RuntimeException(
                    "Instance of %s is not compatible with %s"
                            .formatted(instance.getClass().getName(), clazz.getName())
            );
        }

        SERVICES.put(clazz, instance);
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
