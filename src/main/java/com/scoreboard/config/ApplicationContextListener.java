package com.scoreboard.config;

import ch.qos.logback.classic.LoggerContext;
import com.scoreboard.exception.ApplicationStartupException;
import com.scoreboard.start_initialization.StartupDatabaseInitializer;
import com.scoreboard.util.HibernateUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class ApplicationContextListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(ApplicationContextListener.class);
    private static final String CONTEXT_ATTRIBUTE = "applicationContext";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("=== Application startup started ===");

        try {
            ApplicationContext context = new ApplicationContext();
            log.info("ApplicationContext created with {} services", context.getServiceCount());

            StartupDatabaseInitializer initializer = context.get(StartupDatabaseInitializer.class);
            initializer.initialize();
            log.info("Database initialized with startup data");

            ServletContext servletContext = sce.getServletContext();
            servletContext.setAttribute(CONTEXT_ATTRIBUTE, context);

            log.info("=== Application startup completed successfully ===");
        } catch (Exception e) {
            log.error("Application startup failed", e);
            throw new ApplicationStartupException("Application startup failed", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("=== Application shutdown started ===");

        try {
            HibernateUtil.shutdown();
            log.info("Hibernate shutdown completed");

            ServletContext servletContext = sce.getServletContext();
            servletContext.removeAttribute(CONTEXT_ATTRIBUTE);

            log.info("=== Application shutdown completed successfully ===");
        } catch (Exception e) {
            String errorMessage = "Error during application shutdown";
            log.error(errorMessage, e);
        }
    }
}
