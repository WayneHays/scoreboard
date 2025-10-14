package com.scoreboard.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServiceDiscovery {

    public static Map<Class<?>, Object> discoverServices() {
        Map<Class<?>, Object> services = new ConcurrentHashMap<>();
        ServiceLoader<ServiceProvider> loader = ServiceLoader.load(ServiceProvider.class);

        for (ServiceProvider provider : loader) {
            Class<?> serviceClass = provider.getServiceClass();
            validateAndRegister(services, serviceClass, provider);
        }

        return services;
    }

    private static void validateAndRegister(Map<Class<?>, Object> services, Class<?> serviceClass, Object instance) {
        if (services.containsKey(serviceClass)) {
            throw new RuntimeException("Service already registered: " + serviceClass.getName());
        }

        if (!serviceClass.isInstance(instance)) {
            throw new RuntimeException(
                    "Instance of %s is not compatible with %s"
                            .formatted(instance.getClass().getName(), serviceClass.getName())
            );
        }

        services.put(serviceClass, instance);
    }
}
