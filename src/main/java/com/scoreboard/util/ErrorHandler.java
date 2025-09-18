package com.scoreboard.util;

import com.scoreboard.dto.ErrorPageData;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class ErrorHandler {

    private ErrorHandler() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated");
    }

    public static void handleHttpError(HttpServletRequest req, HttpServletResponse resp,
                                       int statusCode, String message) throws ServletException, IOException {
        ErrorPageData errorData = createErrorPageData(statusCode, message, req);
        renderErrorPage(req, resp, errorData);
    }

    private static ErrorPageData createErrorPageData(int statusCode, String customMessage,
                                                     HttpServletRequest req) {
        String errorIcon;
        String errorTitle;
        String defaultMessage;

        switch (statusCode) {
            case HttpServletResponse.SC_BAD_REQUEST:
                errorIcon = "⚠️";
                errorTitle = "Bad Request";
                defaultMessage = "Your request contains invalid data or parameters.";
                break;
            case HttpServletResponse.SC_NOT_FOUND:
                errorIcon = "❌";
                errorTitle = "Page Not Found";
                defaultMessage = "The page you are looking for doesn't exist or has been moved.";
                break;
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
                errorIcon = "💥";
                errorTitle = "Internal Server Error";
                defaultMessage = "Something went wrong on our server. We're working to fix this issue.";
                break;
            default:
                errorIcon = "⚠️";
                errorTitle = "Error";
                defaultMessage = "An unexpected error occurred.";
        }

        String requestedUrl = buildUrl(req);

        return new ErrorPageData(
                statusCode,
                errorIcon,
                errorTitle,
                defaultMessage,
                customMessage,
                requestedUrl
        );
    }

    private static void renderErrorPage(HttpServletRequest req, HttpServletResponse resp,
                                        ErrorPageData errorData) throws ServletException, IOException {
        resp.setStatus(errorData.statusCode());
        setRequestAttributes(req, errorData);

        req.getServletContext()
                .getRequestDispatcher(WebPaths.ERROR_JSP)
                .forward(req, resp);
    }

    private static void setRequestAttributes(HttpServletRequest req, ErrorPageData data) {
        req.setAttribute("statusCode", data.statusCode());
        req.setAttribute("errorIcon", data.errorIcon());
        req.setAttribute("errorTitle", data.errorTitle());
        req.setAttribute("defaultMessage", data.defaultMessage());
        req.setAttribute("errorMessage", data.errorMessage());
        req.setAttribute("requestedUrl", data.requestedUrl());
    }

    private static String buildUrl(HttpServletRequest req) {
        return req.getRequestURI()
               + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
    }
}
