package com.scoreboard.errorhandler;

import com.scoreboard.dto.ErrorDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ErrorHandler {
    private static final String JSP = "/WEB-INF/error.jsp";
    private static final String DTO = "error";
    private static final String ICON_NOT_FOUND = "❌";
    private static final String ICON_SERVER_ERROR = "💥";
    private static final String ICON_DEFAULT = "⚠️";
    private static final String TITLE_BAD_REQUEST = "Bad Request";
    private static final String TITLE_PAGE_NOT_FOUND = "Page Not Found";
    private static final String TITLE_SERVER_ERROR = "Internal Server Error";
    private static final String TITLE_UNEXPECTED_ERROR = "Unexpected error";
    private static final String MSG_BAD_REQUEST = "Your request contains invalid data or parameters.";
    private static final String MSG_NOT_FOUND = "The page you are looking for doesn't exist or has been moved.";
    private static final String MSG_SERVER_ERROR = "Something went wrong on our server. We're working to fix this issue.";
    private static final String MSG_UNEXPECTED_ERROR = "An unexpected error occurred.";

    public void handleHttpError(HttpServletRequest req, HttpServletResponse resp,
                                       int statusCode, String message) throws ServletException, IOException {
        String errorIcon = getIcon(statusCode);
        String errorTitle = getTitle(statusCode);
        String defaultMessage = getDefaultMessage(statusCode);
        String requestedUrl = buildUrl(req);

        ErrorDto dto = ErrorDto.builder()
                .statusCode(statusCode)
                .icon(errorIcon)
                .title(errorTitle)
                .defaultMessage(defaultMessage)
                .message(message)
                .requestedUrl(requestedUrl)
                .build();

        resp.setStatus(statusCode);
        req.setAttribute(DTO, dto);

        req.getServletContext()
                .getRequestDispatcher(JSP)
                .forward(req, resp);
    }

    private String getIcon(int statusCode) {
        return switch (statusCode) {
            case HttpServletResponse.SC_NOT_FOUND -> ICON_NOT_FOUND;
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR -> ICON_SERVER_ERROR;
            default -> ICON_DEFAULT;
        };
    }

    private String getTitle(int statusCode) {
        return switch (statusCode) {
            case HttpServletResponse.SC_BAD_REQUEST -> TITLE_BAD_REQUEST;
            case HttpServletResponse.SC_NOT_FOUND -> TITLE_PAGE_NOT_FOUND;
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR -> TITLE_SERVER_ERROR;
            default -> TITLE_UNEXPECTED_ERROR;
        };
    }

    private String getDefaultMessage(int statusCode) {
        return switch (statusCode) {
            case HttpServletResponse.SC_BAD_REQUEST -> MSG_BAD_REQUEST;
            case HttpServletResponse.SC_NOT_FOUND -> MSG_NOT_FOUND;
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR -> MSG_SERVER_ERROR;
            default -> MSG_UNEXPECTED_ERROR;
        };
    }

    private String buildUrl(HttpServletRequest req) {
        return req.getRequestURI() +
               (req.getQueryString() != null ? "?" + req.getQueryString() : "");
    }
}
