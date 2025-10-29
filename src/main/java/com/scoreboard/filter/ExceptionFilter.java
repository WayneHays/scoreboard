package com.scoreboard.filter;

import com.scoreboard.exception.NotFoundException;
import com.scoreboard.exception.ScoreboardServiceException;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.util.ErrorHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter(filterName = "ExceptionFilter", urlPatterns = "/*")
public class ExceptionFilter extends HttpFilter {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionFilter.class);
    private static final String GENERIC_ERROR_MESSAGE =
            "An error occurred while processing your request. Please try again later.";

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(req, res);

        } catch (NotFoundException e) {
            logger.debug("Resource not found: {} {} - {}",
                    req.getMethod(), req.getRequestURI(), e.getMessage());
            ErrorHandler.handleHttpError(req, res, SC_NOT_FOUND, e.getMessage());

        } catch (ValidationException e) {
            logger.debug("Validation error: {} {} - {}",
                    req.getMethod(), req.getRequestURI(), e.getMessage());
            ErrorHandler.handleHttpError(req, res, SC_BAD_REQUEST, e.getMessage());

        } catch (ScoreboardServiceException e) {
            logger.error("Service error in request {} {}",
                    req.getMethod(), req.getRequestURI(), e);
            ErrorHandler.handleHttpError(req, res, SC_INTERNAL_SERVER_ERROR, GENERIC_ERROR_MESSAGE);

        } catch (Exception e) {
            logger.error("Unhandled exception in request {} {}",
                    req.getMethod(), req.getRequestURI(), e);
            ErrorHandler.handleHttpError(req, res, SC_INTERNAL_SERVER_ERROR, GENERIC_ERROR_MESSAGE);
        }
    }
}
