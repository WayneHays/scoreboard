package com.scoreboard.filter;

import com.scoreboard.exception.ScoreboardServiceException;
import com.scoreboard.util.ErrorHandler;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebFilter(filterName = "ExceptionFilter", urlPatterns = "/*")
public class ExceptionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (ScoreboardServiceException e) {
            ErrorHandler.handleHttpError(
                    (HttpServletRequest) request,
                    (HttpServletResponse) response,
                    SC_INTERNAL_SERVER_ERROR,
                    "Service error occurred: " + e.getMessage());
        } catch (NumberFormatException e) {
            ErrorHandler.handleHttpError(
                    (HttpServletRequest) request,
                    (HttpServletResponse) response,
                    SC_BAD_REQUEST,
                    "Invalid number format: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            handleValidationError(
                    (HttpServletRequest) request,
                    (HttpServletResponse) response,
                    e);
        } catch (Exception e) {
            ErrorHandler.handleHttpError(
                    (HttpServletRequest) request,
                    (HttpServletResponse) response,
                    SC_INTERNAL_SERVER_ERROR,
                    "Internal server error");
        }
    }

    private void handleValidationError(HttpServletRequest request, HttpServletResponse response,
                                       IllegalArgumentException e) throws ServletException, IOException {
        String message = e.getMessage();

        if (message != null) {
            if (message.contains("UUID is required")) {
                ErrorHandler.handleHttpError(
                        request,
                        response,
                        SC_BAD_REQUEST,
                        "Match ID is required");
            } else if (message.contains("36 characters") || message.contains("UUID must be")) {
                ErrorHandler.handleHttpError(
                        request,
                        response,
                        SC_BAD_REQUEST,
                        "Invalid UUID format");
            } else if (message.toLowerCase().contains("uuid")) {
                ErrorHandler.handleHttpError(
                        request,
                        response,
                        SC_BAD_REQUEST,
                        "Invalid UUID: " + message);
            } else {
                ErrorHandler.handleHttpError(
                        request,
                        response,
                        SC_BAD_REQUEST,
                        "Invalid parameter: " + message);
            }
        } else {
            ErrorHandler.handleHttpError(
                    request,
                    response,
                    SC_BAD_REQUEST,
                    "Invalid request parameter");
        }
    }
}
