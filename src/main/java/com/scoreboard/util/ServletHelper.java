package com.scoreboard.util;

import com.scoreboard.constant.AppDefaults;
import com.scoreboard.exception.ValidationException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServletHelper {
    private static final Logger logger = LoggerFactory.getLogger(ServletHelper.class);
    private static final int UUID_REQUIRED_LENGTH = 36;

    public static UUID parseUuid(String uuidStr, String paramName) {
        if (uuidStr == null || uuidStr.isBlank()) {
            throw new ValidationException("Parameter '" + paramName + "' is required");
        }

        String trimmedUuid = uuidStr.trim();

        if (trimmedUuid.length() != UUID_REQUIRED_LENGTH) {
            logger.warn("Invalid UUID length for parameter '{}': {} (expected exactly 36 characters)",
                    paramName, trimmedUuid.length());
            throw new ValidationException("Invalid UUID format for parameter '" + paramName + "'");
        }

        try {
            return UUID.fromString(trimmedUuid);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid UUID format for parameter '{}': {}", paramName, trimmedUuid);
            throw new ValidationException("Invalid UUID format for parameter '" + paramName + "'");
        }
    }

    public static int parsePageNumber(String pageNumberStr) {
        if (pageNumberStr == null || pageNumberStr.isBlank()) {
            return AppDefaults.DEFAULT_PAGE_NUMBER;
        }

        try {
            return Integer.parseInt(pageNumberStr);
        } catch (NumberFormatException e) {
            logger.warn("Invalid page number format: '{}'", pageNumberStr);
            throw new ValidationException("Invalid page number: " + pageNumberStr, e);
        }
    }

    public static void forwardToJsp(HttpServletRequest req, HttpServletResponse resp, String jspPath)
            throws ServletException, IOException {
        try {
            ServletContext context = req.getServletContext();
            context.getRequestDispatcher(jspPath).forward(req, resp);
        } catch (ServletException | IOException e) {
            logger.error("Failed to forward to JSP: {}", jspPath, e);
            throw e;
        }
    }

    public static void redirect(HttpServletRequest req, HttpServletResponse resp, String url)
            throws IOException {
        try {
            String contextPath = req.getContextPath();
            String fullUrl = contextPath + url;
            resp.sendRedirect(fullUrl);
        } catch (IOException e) {
            logger.error("Failed to redirect to: {}", url, e);
            throw e;
        }
    }
}
