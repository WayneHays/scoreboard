package com.scoreboard.config.context;

import com.scoreboard.config.lifecycle.LifecycleComponent;
import com.scoreboard.exception.ApplicationStartupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ApplicationLifecycleManager {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationLifecycleManager.class);
    private final List<LifecycleComponent> components;

    public ApplicationLifecycleManager(List<LifecycleComponent> components) {
        this.components = new ArrayList<>(components);
    }

    public ApplicationContext startup() {
        logger.info("=== Application startup started ===");

        try {
            ApplicationContext context = initializeContext();
            startComponents(context);

            logger.info("=== Application startup completed successfully ===");
            return context;

        } catch (ApplicationStartupException e) {
            logger.error("Application startup failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Application startup failed", e);
            throw new ApplicationStartupException("Application startup failed", e);
        }
    }

    public void shutdown() {
        logger.info("=== Application shutdown started ===");
        stopComponents();
        logger.info("=== Application shutdown completed ===");
    }

    private ApplicationContext initializeContext() {
        ApplicationContext context = new ApplicationContextInitializer().initialize();
        logger.info("ApplicationContext created with {} services", context.getServiceCount());
        return context;
    }

    private void startComponents(ApplicationContext context) {
        for (LifecycleComponent component : components) {
            try {
                component.start(context);
            } catch (Exception e) {
                logger.error("Failed to start component: {}", component.getName(), e);
                throw new ApplicationStartupException(
                        "Failed to start component: " + component.getName(), e);
            }
        }
    }

    private void stopComponents() {
        for (int i = components.size() - 1; i >= 0; i--) {
            LifecycleComponent component = components.get(i);
            try {
                component.stop();
            } catch (Exception e) {
                logger.error("Error stopping component: {}", component.getName(), e);
            }
        }
    }
}
