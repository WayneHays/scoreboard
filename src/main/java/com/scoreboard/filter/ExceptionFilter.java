package com.scoreboard.filter;

import com.scoreboard.exception.ScoreboardServiceException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebFilter("/*")
public class ExceptionFilter implements Filter {
    private static final String ERROR_CODE_ATTRIBUTE = "errorCode";
    private static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";
    public static final String REQUESTED_URL_ATTRIBUTE = "requestedUrl";
    public static final String EXCEPTION_MESSAGE_ATTRIBUTE = "exceptionMessage";
    public static final String SERVICE_ERROR_JSP = "/WEB-INF/error/500.jsp";
    public static final String NOT_FOUND_JSP = "/WEB-INF/error/404.jsp";
    public static final String SERVICE_ERROR_MESSAGE = "Service error occurred";
    public static final String RUNTIME_ERROR_MESSAGE = "Internal server error";

    private static final Set<String> VALID_URLS = Set.of(
            "/home",
            "/matches",
            "/new-match",
            "/match",
            "/match-score"
    );

    private static final Set<String> STATIC_EXTENSIONS = Set.of(
            ".css", ".js", ".png", ".jpg", ".jpeg", ".gif", ".ico", ".svg"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        if (isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (isValidUrl(path)) {
            try {
                chain.doFilter(request, response);
            } catch (ScoreboardServiceException e) {
                handleError(httpRequest, httpResponse, SERVICE_ERROR_MESSAGE, e.getMessage());
            } catch (RuntimeException e) {
                handleError(httpRequest, httpResponse, RUNTIME_ERROR_MESSAGE, e.getMessage());
            }
        } else {
            handle404Error(httpRequest, httpResponse);
        }
    }

    private boolean isValidUrl(String path) {
        String cleanPath = path.contains("?") ? path.substring(0, path.indexOf("?")) : path;
        return VALID_URLS.contains(cleanPath);
    }

    private boolean isStaticResource(String path) {
        for (String extension : STATIC_EXTENSIONS) {
            if (path.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.startsWith("/static/");
    }

    private void handle404Error(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.setAttribute(ERROR_CODE_ATTRIBUTE, 404);
        request.setAttribute(ERROR_MESSAGE_ATTRIBUTE, "Page not found");
        request.setAttribute(REQUESTED_URL_ATTRIBUTE, request.getRequestURI());
        request.getRequestDispatcher(NOT_FOUND_JSP).forward(request, response);
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response,
                             String errorMessage, String exceptionMessage) throws ServletException, IOException {
        response.setStatus(500);
        request.setAttribute(ERROR_CODE_ATTRIBUTE, 500);
        request.setAttribute(ERROR_MESSAGE_ATTRIBUTE, errorMessage);
        if (exceptionMessage != null) {
            request.setAttribute(EXCEPTION_MESSAGE_ATTRIBUTE, exceptionMessage);
        }
        request.setAttribute(REQUESTED_URL_ATTRIBUTE, request.getRequestURI());
        request.getRequestDispatcher(ExceptionFilter.SERVICE_ERROR_JSP).forward(request, response);
    }
}
