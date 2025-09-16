package com.scoreboard.filter;

import com.scoreboard.util.ErrorHandler;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebFilter(filterName = "UrlValidationFilter", urlPatterns = "/*")
public class UrlValidationFilter implements Filter {
    private static final Set<String> VALID_URLS = Set.of(
            "/",
            "index.html",
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
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        if (isStaticResource(path)) {
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }

        if (isValidUrl(path)) {
            filterChain.doFilter(httpRequest, httpResponse);
        } else {
            ErrorHandler.handleHttpError(httpRequest, httpResponse, HttpServletResponse.SC_NOT_FOUND, "Page not found");
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
}
