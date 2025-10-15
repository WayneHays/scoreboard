package com.scoreboard.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    public static void discoverAndRegister(ApplicationContext context) {
        logger.info("Starting service discovery...");

        ServiceLoader<ServiceProvider> providers = ServiceLoader.load(ServiceProvider.class);
        int registeredCount = processProviders(context, providers);

        logger.info("Service discovery completed. Registered {} services", registeredCount);
    }

    private static int processProviders(ApplicationContext context, ServiceLoader<ServiceProvider> providers) {
        int count = 0;

        for (ServiceProvider provider : providers) {
            registerProvider(context, provider);
            count++;
        }

        return count;
    }

    private static void registerProvider(ApplicationContext context, ServiceProvider provider) {
        try {
            Class<?> serviceType = provider.getServiceType();
            Object serviceInstance = provider.createService(context);

            registerInContext(context, serviceType, serviceInstance);
            logger.debug("Registered service: {}", serviceType.getName());

        } catch (Exception e) {
            String providerName = provider.getClass().getName();
            logger.error("Failed to register service provider: {}", providerName, e);
            throw new IllegalStateException("Service provider failed: " + providerName, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void registerInContext(ApplicationContext context, Class<?> type, Object instance) {
        context.register((Class<T>) type, (T) instance);
    }
}
