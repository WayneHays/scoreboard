package com.scoreboard.filter;

import com.scoreboard.exception.NotFoundException;
import com.scoreboard.exception.ScoreboardServiceException;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.util.ErrorHandler;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter(filterName = "ExceptionFilter", urlPatterns = "/*")
public class ExceptionFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(req, res);
        } catch (NotFoundException e) {
            ErrorHandler.handleHttpError(
                    req,
                    res,
                    SC_NOT_FOUND,
                    e.getMessage());
        } catch (ValidationException e) {
            ErrorHandler.handleHttpError(
                    req,
                    res,
                    SC_BAD_REQUEST,
                    e.getMessage());
        } catch (ScoreboardServiceException e) {
            ErrorHandler.handleHttpError(
                    req,
                    res,
                    SC_INTERNAL_SERVER_ERROR,
                    "Service error occurred: " + e.getMessage());
        } catch (NumberFormatException e) {
            ErrorHandler.handleHttpError(
                    req,
                    res,
                    SC_BAD_REQUEST,
                    "Invalid number format");
        } catch (IllegalArgumentException e) {
            ErrorHandler.handleHttpError(
                    req,
                    res,
                    SC_BAD_REQUEST,
                    e.getMessage());
        } catch (Exception e) {
            ErrorHandler.handleHttpError(
                    req,
                    res,
                    SC_INTERNAL_SERVER_ERROR,
                    "Internal server error");
        }
    }
}
