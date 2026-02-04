package com.scoreboard.errorhandler;

import com.scoreboard.dto.ErrorDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ErrorHandler {
    private static final String ERROR_JSP = "/WEB-INF/error.jsp";
    private static final String ERROR_DTO = "errorDto";

    private static final String NOT_FOUND_ICON = "❌";
    private static final String SERVER_ERROR_ICON = "💥";
    private static final String DEFAULT_ICON = "⚠️";

    private static final String BAD_REQUEST_TITLE = "Bad Request";
    private static final String PAGE_NOT_FOUND_TITLE = "Page Not Found";
    private static final String SERVER_ERROR_TITLE = "Internal Server Error";
    private static final String UNEXPECTED_ERROR_TITLE = "Unexpected error";

    private static final String BAD_REQUEST_MESSAGE = "Your request contains invalid data or parameters.";
    private static final String NOT_FOUND_MESSAGE = "The page you are looking for doesn't exist or has been moved.";
    private static final String SERVER_ERROR_MESSAGE = "Something went wrong on our server. We're working to fix this issue.";
    private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred.";

    public void handleHttpError(HttpServletRequest req, HttpServletResponse resp,
                                       int statusCode, String message) throws ServletException, IOException {
        String errorIcon = getErrorIcon(statusCode);
        String errorTitle = getErrorTitle(statusCode);
        String defaultMessage = getDefaultMessage(statusCode);
        String requestedUrl = buildUrl(req);

        ErrorDto dto = ErrorDto.builder()
                .statusCode(statusCode)
                .errorIcon(errorIcon)
                .errorTitle(errorTitle)
                .defaultMessage(defaultMessage)
                .message(message)
                .requestedUrl(requestedUrl)
                .build();

        resp.setStatus(statusCode);
        req.setAttribute(ERROR_DTO, dto);

        req.getServletContext()
                .getRequestDispatcher(ERROR_JSP)
                .forward(req, resp);
    }

    private String getErrorIcon(int statusCode) {
        return switch (statusCode) {
            case HttpServletResponse.SC_NOT_FOUND -> NOT_FOUND_ICON;
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR -> SERVER_ERROR_ICON;
            default -> DEFAULT_ICON;
        };
    }

    private String getErrorTitle(int statusCode) {
        return switch (statusCode) {
            case HttpServletResponse.SC_BAD_REQUEST -> BAD_REQUEST_TITLE;
            case HttpServletResponse.SC_NOT_FOUND -> PAGE_NOT_FOUND_TITLE;
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR -> SERVER_ERROR_TITLE;
            default -> UNEXPECTED_ERROR_TITLE;
        };
    }

    private String getDefaultMessage(int statusCode) {
        return switch (statusCode) {
            case HttpServletResponse.SC_BAD_REQUEST -> BAD_REQUEST_MESSAGE;
            case HttpServletResponse.SC_NOT_FOUND -> NOT_FOUND_MESSAGE;
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR -> SERVER_ERROR_MESSAGE;
            default -> UNEXPECTED_ERROR_MESSAGE;
        };
    }

    private String buildUrl(HttpServletRequest req) {
        return req.getRequestURI() +
               (req.getQueryString() != null ? "?" + req.getQueryString() : "");
    }
}
