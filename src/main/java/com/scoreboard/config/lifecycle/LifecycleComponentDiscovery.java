package com.scoreboard.config.lifecycle;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LifecycleComponentDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(LifecycleComponentDiscovery.class);

    public static List<LifecycleComponent> discoverComponents() {
        logger.info("Discovering lifecycle components");

        List<LifecycleComponentProvider> providers = new ArrayList<>();
        ServiceLoader<LifecycleComponentProvider> loader =
                ServiceLoader.load(LifecycleComponentProvider.class);

        for (LifecycleComponentProvider provider : loader) {
            providers.add(provider);
        }

        providers.sort(Comparator.comparingInt(LifecycleComponentProvider::getOrder));

        List<LifecycleComponent> components = providers.stream()
                .map(LifecycleComponentProvider::createComponent)
                .toList();

        logger.info("Discovered {} lifecycle components", components.size());
        return components;
    }
}
