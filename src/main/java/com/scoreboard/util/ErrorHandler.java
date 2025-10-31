package com.scoreboard.util;

import com.scoreboard.constant.JspPaths;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorHandler {

    public static void handleHttpError(HttpServletRequest req, HttpServletResponse resp,
                                       int statusCode, String message) throws ServletException, IOException {
        String errorIcon = getErrorIcon(statusCode);
        String errorTitle = getErrorTitle(statusCode);
        String defaultMessage = getDefaultMessage(statusCode);
        String requestedUrl = buildUrl(req);

        resp.setStatus(statusCode);
        req.setAttribute("statusCode", statusCode);
        req.setAttribute("errorIcon", errorIcon);
        req.setAttribute("errorTitle", errorTitle);
        req.setAttribute("defaultMessage", defaultMessage);
        req.setAttribute("errorMessage", message);
        req.setAttribute("requestedUrl", requestedUrl);

        req.getServletContext()
                .getRequestDispatcher(JspPaths.ERROR_JSP)
                .forward(req, resp);
    }

    private static String getErrorIcon(int statusCode) {
        return switch (statusCode) {
            case HttpServletResponse.SC_NOT_FOUND -> "❌";
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR -> "💥";
            default -> "⚠️";
        };
    }

    private static String getErrorTitle(int statusCode) {
        return switch (statusCode) {
            case HttpServletResponse.SC_BAD_REQUEST -> "Bad Request";
            case HttpServletResponse.SC_NOT_FOUND -> "Page Not Found";
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR -> "Internal Server Error";
            default -> "Unexpected error";
        };
    }

    private static String getDefaultMessage(int statusCode) {
        return switch (statusCode) {
            case HttpServletResponse.SC_BAD_REQUEST ->
                    "Your request contains invalid data or parameters.";
            case HttpServletResponse.SC_NOT_FOUND ->
                    "The page you are looking for doesn't exist or has been moved.";
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR ->
                    "Something went wrong on our server. We're working to fix this issue.";
            default ->
                    "An unexpected error occurred.";
        };
    }

    private static String buildUrl(HttpServletRequest req) {
        return req.getRequestURI() +
               (req.getQueryString() != null ? "?" + req.getQueryString() : "");
    }
}
