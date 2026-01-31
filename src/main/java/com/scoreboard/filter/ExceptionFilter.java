package com.scoreboard.filter;

import com.scoreboard.exception.MatchNotFoundException;
import com.scoreboard.exception.PlayerNotInMatchException;
import com.scoreboard.exception.UuidParsingException;
import com.scoreboard.util.ErrorHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@Slf4j
@WebFilter(filterName = "ExceptionFilter", urlPatterns = "/*")
public class ExceptionFilter extends HttpFilter {
    private static final String MSG_INVALID_PAGE_NUMBER = "Incorrect page number format";
    private static final String MSG_MATCH_NOT_FOUND = "Match not found";
    private static final String MSG_INVALID_UUID_FORMAT = "Incorrect UUID format";
    private static final String MSG_PLAYER_NOT_IN_MATCH = "Scorer is not playing in this match";
    private static final String MSG_GENERIC_ERROR = "An error occurred while processing your request. Please try again later.";

    private static final String LOG_INVALID_PAGE_NUMBER = "Invalid page number format in request: {}";
    private static final String LOG_MATCH_NOT_FOUND = "Match not found: {} {} - {}";
    private static final String LOG_INVALID_UUID = "Invalid match uuid format in request: {}";
    private static final String LOG_PLAYER_NOT_IN_MATCH = "Attempt to award point to player, who is not part of match {} {}";
    private static final String LOG_UNHANDLED_EXCEPTION = "Unhandled exception in request {} {}";

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(req, res);
        } catch (NumberFormatException e) {
            log.error(LOG_INVALID_PAGE_NUMBER, req.getRequestURI(), e);
            ErrorHandler.handleHttpError(req, res, SC_BAD_REQUEST, MSG_INVALID_PAGE_NUMBER);
        } catch (MatchNotFoundException e) {
            log.error(LOG_MATCH_NOT_FOUND, req.getMethod(), req.getRequestURI(), e.getMessage(), e);
            ErrorHandler.handleHttpError(req, res, SC_NOT_FOUND, MSG_MATCH_NOT_FOUND);
        } catch (UuidParsingException e) {
            log.error(LOG_INVALID_UUID, req.getRequestURI(), e);
            ErrorHandler.handleHttpError(req, res, SC_BAD_REQUEST, MSG_INVALID_UUID_FORMAT);
        } catch (PlayerNotInMatchException e) {
            log.error(LOG_PLAYER_NOT_IN_MATCH, req.getMethod(), req.getRequestURI(), e);
            ErrorHandler.handleHttpError(req, res, SC_BAD_REQUEST, MSG_PLAYER_NOT_IN_MATCH);
        } catch (Exception e) {
            log.error(LOG_UNHANDLED_EXCEPTION, req.getMethod(), req.getRequestURI(), e);
            ErrorHandler.handleHttpError(req, res, SC_INTERNAL_SERVER_ERROR, MSG_GENERIC_ERROR);
        }
    }
}
