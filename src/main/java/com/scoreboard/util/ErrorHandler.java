package com.scoreboard.util;

import com.scoreboard.dto.ErrorPageData;
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
        ErrorPageData errorPageData = createErrorPageData(statusCode, message, req);
        renderErrorPage(req, resp, errorPageData);
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

        return ErrorPageData.builder()
                .statusCode(statusCode)
                .errorIcon(errorIcon)
                .errorTitle(errorTitle)
                .defaultMessage(defaultMessage)
                .errorMessage(customMessage)
                .requestedUrl(requestedUrl)
                .build();
    }

    private static void renderErrorPage(HttpServletRequest req, HttpServletResponse resp,
                                        ErrorPageData errorPageData) throws ServletException, IOException {
        resp.setStatus(errorPageData.statusCode());
        req.setAttribute("errorPageData", errorPageData);

        req.getServletContext()
                .getRequestDispatcher(WebPaths.ERROR_JSP)
                .forward(req, resp);
    }


    private static String buildUrl(HttpServletRequest req) {
        return req.getRequestURI()
               + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
    }
}
