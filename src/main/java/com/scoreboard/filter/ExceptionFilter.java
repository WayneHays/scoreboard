package com.scoreboard.filter;

import com.scoreboard.exception.NotFoundException;
import com.scoreboard.exception.ScoreboardServiceException;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.util.ErrorHandler;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter(filterName = "ExceptionFilter", urlPatterns = "/*")
public class ExceptionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (NotFoundException e) {
            ErrorHandler.handleHttpError(
                    (HttpServletRequest) request,
                    (HttpServletResponse) response,
                    SC_NOT_FOUND,
                    e.getMessage());
        } catch (ValidationException e) {
            ErrorHandler.handleHttpError(
                    (HttpServletRequest) request,
                    (HttpServletResponse) response,
                    SC_BAD_REQUEST,
                    e.getMessage());
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
                    "Invalid number format");
        } catch (IllegalArgumentException e) {
            ErrorHandler.handleHttpError(
                    (HttpServletRequest) request,
                    (HttpServletResponse) response,
                    SC_BAD_REQUEST,
                    e.getMessage());
        } catch (Exception e) {
            ErrorHandler.handleHttpError(
                    (HttpServletRequest) request,
                    (HttpServletResponse) response,
                    SC_INTERNAL_SERVER_ERROR,
                    "Internal server error");
        }
    }
}
