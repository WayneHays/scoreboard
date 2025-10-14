package com.scoreboard.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ServiceLoader;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServiceDiscovery {

    public static void discoverAndRegister(ApplicationContext context) {
        ServiceLoader<ServiceProvider> loader = ServiceLoader.load(ServiceProvider.class);

        for (ServiceProvider provider : loader) {
            try {
                Class<?> type = provider.getServiceType();
                Object service = provider.createService(context);
                registerService(context, type, service);
            } catch (Exception e) {
                throw new IllegalStateException(
                        "Service provider failed: " + provider.getClass().getName(), e
                );
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void registerService(
            ApplicationContext context,
            Class<?> type,
            Object instance) {
        context.register((Class<T>) type, (T) instance);
    }
}
