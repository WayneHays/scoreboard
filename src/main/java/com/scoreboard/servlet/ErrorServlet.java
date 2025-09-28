package com.scoreboard.servlet;

import com.scoreboard.util.ErrorHandler;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet ("/error")
public class ErrorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handleError(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handleError(req, resp);
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Integer statusCode = (Integer) req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMessage = (String) req.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        if (statusCode == null) {
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }

        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            errorMessage = getDefaultMessage(statusCode);
        }

        ErrorHandler.handleHttpError(req, resp, statusCode, errorMessage);
    }

    private String getDefaultMessage(int statusCode) {
        return switch (statusCode) {
            case HttpServletResponse.SC_NOT_FOUND -> "Page not found";
            case HttpServletResponse.SC_INTERNAL_SERVER_ERROR -> "Internal server error";
            case HttpServletResponse.SC_BAD_REQUEST -> "Bad request";
            default -> "An error occurred";
        };
    }
}
