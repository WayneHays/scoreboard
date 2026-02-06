package com.scoreboard.servlet;

import com.scoreboard.context.ApplicationContext;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseServlet extends HttpServlet {
    private static final String PATH_DELIMITER = "/";
    private static final String PARAMETER_PREFIX = "?";
    private static final String PARAMETER_EQUAL = "=";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String JSP_SUB_PATH = "/WEB-INF" + PATH_DELIMITER;
    private static final String JSP_EXTENSION = ".jsp";
    private static final String MSG_CONTEXT_NOT_FOUND = "ApplicationContext not found in ServletContext";
    private static final String MSG_SERVLET_INIT_SUCCESS = "Servlet {} initialized successfully";

    @Override
    public void init() throws ServletException {
        super.init();
        getApplicationContext();
        log.debug(MSG_SERVLET_INIT_SUCCESS, getClass().getSimpleName());
    }

    protected void forwardTo(String pageName, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(JSP_SUB_PATH + pageName + JSP_EXTENSION).forward(request, response);
    }

    protected void redirectTo(String servletPath, Map<String, ?> requestParameters,
                              HttpServletRequest request, HttpServletResponse response) throws IOException {
        String parameters = buildParameters(requestParameters);
        String fullUrl = request.getContextPath() + servletPath + parameters;

        response.sendRedirect(fullUrl);
    }

    private String buildParameters(Map<String, ?> requestParameters) {
        if (requestParameters.isEmpty()) {
            return "";
        }

        return PARAMETER_PREFIX +
               requestParameters.entrySet().stream()
                       .filter(this::isValidEntry)
                       .map(this::buildParameter)
                       .collect(Collectors.joining(PARAMETER_DELIMITER));
    }

    private boolean isValidEntry(Map.Entry<String, ?> entry) {
        return ObjectUtils.isNotEmpty(entry.getKey()) && ObjectUtils.isNotEmpty(entry.getValue());
    }

    private String buildParameter(Map.Entry<String, ?> parameterEntry) {
        String encodedKey = encodeForUrl(parameterEntry.getKey());
        String encodedValue = encodeForUrl(parameterEntry.getValue().toString());

        return encodedKey + PARAMETER_EQUAL + encodedValue;
    }

    private String encodeForUrl(String raw) {
        return URLEncoder.encode(raw, StandardCharsets.UTF_8);
    }

    protected <T> T getService(Class<T> serviceClass) {
        ApplicationContext context = getApplicationContext();
        return context.get(serviceClass);
    }

    private ApplicationContext getApplicationContext() {
        ServletContext servletContext = getServletContext();
        ApplicationContext context = (ApplicationContext) servletContext.getAttribute(ServletContext.class.getSimpleName());

        if (context == null) {
            String message = MSG_CONTEXT_NOT_FOUND;
            log.error(message);
            throw new IllegalStateException(message);
        }
        return context;
    }
}
