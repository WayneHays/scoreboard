package com.scoreboard.util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class ErrorHandler {

    private ErrorHandler() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void handleHttpError(HttpServletRequest req, HttpServletResponse resp,
                                       int statusCode, String message)
            throws ServletException, IOException {
        resp.setStatus(statusCode);
        req.setAttribute("statusCode", statusCode);
        req.setAttribute("errorMessage", message);
        req.setAttribute("requestedUrl", buildUrl(req));
        setErrorPageAttributes(req, statusCode);
        req.getServletContext().getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
    }

    public static void setErrorPageAttributes(HttpServletRequest request, int statusCode) {
        String errorIcon;
        String errorTitle;
        String defaultMessage;

        switch (statusCode) {
            case 400:
                errorIcon = "⚠️";
                errorTitle = "Bad Request";
                defaultMessage = "Your request contains invalid data or parameters.";
                break;
            case 404:
                errorIcon = "❌";
                errorTitle = "Page Not Found";
                defaultMessage = "The page you are looking for doesn't exist or has been moved.";
                break;
            case 500:
                errorIcon = "💥";
                errorTitle = "Internal Server Error";
                defaultMessage = "Something went wrong on our server. We're working to fix this issue.";
                break;
            default:
                errorIcon = "⚠️";
                errorTitle = "Error";
                defaultMessage = "An unexpected error occurred.";
        }

        request.setAttribute("errorIcon", errorIcon);
        request.setAttribute("errorTitle", errorTitle);
        request.setAttribute("defaultMessage", defaultMessage);
        request.setAttribute("statusCode", statusCode);
    }

    private static String buildUrl(HttpServletRequest req) {
        return req.getRequestURI() +
               (req.getQueryString() != null ? "?" + req.getQueryString() : "");
    }
}
