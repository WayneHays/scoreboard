package com.scoreboard.servlet;

import com.scoreboard.config.ApplicationContext;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BaseServlet.class);
    private static final String CONTEXT_ATTRIBUTE = "applicationContext";

    @Override
    public void init() throws ServletException {
        super.init();
        logger.debug("Servlet {} initialized successfully", getClass().getSimpleName());
    }

    protected <T> T getService(Class<T> serviceClass) {
        ApplicationContext context = getApplicationContext();
        return context.get(serviceClass);
    }

    private ApplicationContext getApplicationContext() {
        ServletContext servletContext = getServletContext();
        ApplicationContext context = (ApplicationContext) servletContext.getAttribute(CONTEXT_ATTRIBUTE);

        if (context == null) {
            logger.error("ApplicationContext not found in ServletContext");
            throw new IllegalStateException("ApplicationContext not found in ServletContext");
        }

        return context;
    }
}
