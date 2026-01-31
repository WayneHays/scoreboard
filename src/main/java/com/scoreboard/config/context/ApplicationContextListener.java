package com.scoreboard.config.context;

import com.scoreboard.config.lifecycle.*;
import com.scoreboard.constant.JspPaths;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Slf4j
@WebListener
public class ApplicationContextListener implements ServletContextListener {
    private final ApplicationLifecycleManager lifecycleManager;

    public ApplicationContextListener() {
        List<LifecycleComponent> components = LifecycleComponentDiscovery.discoverComponents();
        this.lifecycleManager = new ApplicationLifecycleManager(components);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ApplicationContext context = lifecycleManager.startup();

        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute(ServletContext.class.getSimpleName(), context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        cleanupServletContext(sce);
        lifecycleManager.shutdown();
    }

    private void cleanupServletContext(ServletContextEvent sce) {
        try {
            ServletContext servletContext = sce.getServletContext();
            servletContext.removeAttribute(ServletContext.class.getSimpleName());
            log.debug("Application context removed from servlet context");
        } catch (Exception e) {
            log.error("Error removing application context", e);
        }
    }
}
