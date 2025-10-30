package com.scoreboard.config.servicediscovery;

import com.scoreboard.config.context.ApplicationContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    public static void discoverAndRegister(ApplicationContext context) {
        logger.info("Starting service discovery");

        int count = 0;
        ServiceLoader<ServiceProvider> providers = ServiceLoader.load(ServiceProvider.class);

        for (ServiceProvider provider : providers) {
            try {
                registerService(context, provider);
                count++;
            } catch (Exception e) {
                String message = "Failed to register service from provider: %s";
                logger.error(message.formatted(provider.getClass().getSimpleName()), e);
                throw new IllegalStateException(message, e);
            }
        }

        logger.info("Service discovery completed. Registered {} services", count);
    }

    @SuppressWarnings("unchecked")
    private static <T> void registerService(ApplicationContext context, ServiceProvider provider) {
        Class<?> type = provider.getServiceType();
        Object instance = provider.createService(context);
        context.register((Class<T>) type, (T) instance);
    }
}
