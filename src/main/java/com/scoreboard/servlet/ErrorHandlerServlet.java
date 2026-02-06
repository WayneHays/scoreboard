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
    private static final String ATTR_STATUS_CODE = "jakarta.servlet.error.status_code";
    private static final String ATTR_ERROR_MSG = "jakarta.servlet.error.message";
    private static final String ATTR_EXCEPTION = "jakarta.servlet.error.exception";
    private static final String ATTR_URI = "jakarta.servlet.error.request_uri";
    private static final String MSG_ERROR = "Error {} for URI: {}, message: {}";

    private ErrorHandler errorHandler;

    @Override
    public void init() throws ServletException {
        super.init();
        this.errorHandler = getService(ErrorHandler.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer statusCode = (Integer) req.getAttribute(ATTR_STATUS_CODE);
        String message = (String) req.getAttribute(ATTR_ERROR_MSG);
        Throwable throwable = (Throwable) req.getAttribute(ATTR_EXCEPTION);
        String uri = (String) req.getAttribute(ATTR_URI);

        if (statusCode == null) {
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }

        log.error(MSG_ERROR, statusCode, uri, message, throwable);

        errorHandler.handleHttpError(req, resp, statusCode, message);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
