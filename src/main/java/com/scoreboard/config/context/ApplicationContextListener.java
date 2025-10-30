package com.scoreboard.config.context;

import com.scoreboard.config.lifecycle.*;
import com.scoreboard.constant.WebPaths;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@WebListener
public class ApplicationContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationContextListener.class);
    private final ApplicationLifecycleManager lifecycleManager;

    public ApplicationContextListener() {
        List<LifecycleComponent> components = LifecycleComponentDiscovery.discoverComponents();
        this.lifecycleManager = new ApplicationLifecycleManager(components);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ApplicationContext context = lifecycleManager.startup();

        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute(WebPaths.APPLICATION_CONTEXT_ATTR, context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        cleanupServletContext(sce);
        lifecycleManager.shutdown();
    }

    private void cleanupServletContext(ServletContextEvent sce) {
        try {
            ServletContext servletContext = sce.getServletContext();
            servletContext.removeAttribute(WebPaths.APPLICATION_CONTEXT_ATTR);
            logger.debug("Application context removed from servlet context");
        } catch (Exception e) {
            logger.error("Error removing application context", e);
        }
    }
}
