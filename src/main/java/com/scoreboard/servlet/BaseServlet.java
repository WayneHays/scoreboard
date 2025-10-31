package com.scoreboard.servlet;

import com.scoreboard.config.context.ApplicationContext;
import com.scoreboard.constant.JspPaths;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BaseServlet.class);

    @Override
    public void init() throws ServletException {
        super.init();
        getApplicationContext();
        logger.debug("Servlet {} initialized successfully", getClass().getSimpleName());
    }

    protected <T> T getService(Class<T> serviceClass) {
        ApplicationContext context = getApplicationContext();
        return context.get(serviceClass);
    }

    private ApplicationContext getApplicationContext() {
        ServletContext servletContext = getServletContext();
        ApplicationContext context = (ApplicationContext) servletContext.getAttribute(JspPaths.APPLICATION_CONTEXT_ATTR);

        if (context == null) {
            String message = "ApplicationContext not found in ServletContext";
            logger.error(message);
            throw new IllegalStateException(message);
        }
        return context;
    }
}
