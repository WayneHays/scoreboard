package com.scoreboard.filter;

import com.scoreboard.exception.ScoreboardServiceException;
import com.scoreboard.util.JspPaths;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebFilter(filterName = "ExceptionFilter", urlPatterns = "/*")
public class ExceptionFilter implements Filter {
    private static final String ERROR_CODE_ATTR = "errorCode";
    private static final String ERROR_MESSAGE_ATTR = "errorMessage";
    private static final String REQUESTED_URL_ATTR = "requestedUrl";
    private static final String EXCEPTION_MESSAGE_ATTR = "exceptionMessage";

    private static final String SERVICE_ERROR_MSG = "Service error occurred";
    private static final String RUNTIME_ERROR_MSG = "Internal server error";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (ScoreboardServiceException e) {
            handleError(request, response, SERVICE_ERROR_MSG, e.getMessage());
        } catch (RuntimeException e) {
            handleError(request, response, RUNTIME_ERROR_MSG, e.getMessage());
        }
    }

    private void handleError(ServletRequest request, ServletResponse response,
                             String errorMessage, String exceptionMessage)
            throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.setStatus(SC_INTERNAL_SERVER_ERROR);
        httpRequest.setAttribute(ERROR_CODE_ATTR, SC_INTERNAL_SERVER_ERROR);
        httpRequest.setAttribute(ERROR_MESSAGE_ATTR, errorMessage);

        if (exceptionMessage != null) {
            httpRequest.setAttribute(EXCEPTION_MESSAGE_ATTR, exceptionMessage);
        }

        httpRequest.setAttribute(REQUESTED_URL_ATTR, httpRequest.getRequestURI());
        httpRequest.getRequestDispatcher(JspPaths.ERROR_500_JSP).forward(request, response);
    }
}
