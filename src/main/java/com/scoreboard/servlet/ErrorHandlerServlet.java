package com.scoreboard.servlet;

import com.scoreboard.errorhandler.ErrorHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@WebServlet("/error")
@Slf4j
public class ErrorHandlerServlet extends BaseServlet {
    private ErrorHandler errorHandler;

    @Override
    public void init() throws ServletException {
        super.init();
        this.errorHandler = getService(ErrorHandler.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer statusCode = (Integer) req.getAttribute("jakarta.servlet.error.status_code");
        String message = (String) req.getAttribute("jakarta.servlet.error.message");
        Throwable throwable = (Throwable) req.getAttribute("jakarta.servlet.error.exception");
        String requestUri = (String) req.getAttribute("jakarta.servlet.error.request_uri");

        if (statusCode == null) {
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }

        log.error("Error {} for URI: {}, message: {}", statusCode, requestUri, message, throwable);

        errorHandler.handleHttpError(req, resp, statusCode, message);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
