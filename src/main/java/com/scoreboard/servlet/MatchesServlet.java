package com.scoreboard.servlet;

import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.service.FindMatchesService;
import com.scoreboard.service.PlayerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebServlet("/matches")
public class MatchesServlet extends HttpServlet {
    private static final String PAGE_ATTRIBUTE = "page";
    private static final String MATCHES_ATTRIBUTE = "matches";
    private static final String TOTAL_COUNT_OF_PAGES_ATTRIBUTE = "totalCountOfPages";
    private static final String ERROR_MESSAGE_ATTRIBUTE = "errorMessage";
    private static final String FILTER_BY_PLAYER_NAME_ATTRIBUTE = "filter_by_player_name";
    private static final String PLAYER_NOT_FOUND_MESSAGE = "Player not found : ";
    private static final String MATCHES_JSP = "/WEB-INF/matches.jsp";

    private FindMatchesService findMatchesService = FindMatchesService.getInstance();
    private PlayerService playerService = PlayerService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String playerName = req.getParameter(FILTER_BY_PLAYER_NAME_ATTRIBUTE);
        String pageNumberStr = req.getParameter(PAGE_ATTRIBUTE);
        int pageNumber;

        try {
            pageNumber = Integer.parseInt(pageNumberStr);
        } catch (NumberFormatException e) {
            pageNumber = 1;
        }

        if (playerName == null || playerName.trim().isEmpty()) {
            showMatchesByPage(req, resp, pageNumber);
        } else {
            showMatchesByPlayerByPage(req, resp, playerName, pageNumber);
        }
    }

    private void showMatchesByPage(HttpServletRequest req, HttpServletResponse resp, int pageNumber)
            throws ServletException, IOException {
        List<Match> matches = findMatchesService.findMatchesByPage(pageNumber);
        int totalCountOfPages = findMatchesService.getTotalCountOfPages();

        req.setAttribute(PAGE_ATTRIBUTE, pageNumber);
        req.setAttribute(MATCHES_ATTRIBUTE, matches);
        req.setAttribute(TOTAL_COUNT_OF_PAGES_ATTRIBUTE, totalCountOfPages);
        getServletContext().getRequestDispatcher(MATCHES_JSP).forward(req, resp);
    }

    private void showMatchesByPlayerByPage(HttpServletRequest req, HttpServletResponse resp, String playerName, int pageNumber)
            throws ServletException, IOException {
        Optional<Player> maybePlayer = playerService.find(playerName);

        if (maybePlayer.isEmpty()) {
            req.setAttribute(ERROR_MESSAGE_ATTRIBUTE, PLAYER_NOT_FOUND_MESSAGE + playerName);
            req.setAttribute(MATCHES_ATTRIBUTE, new ArrayList<>());
            req.setAttribute(TOTAL_COUNT_OF_PAGES_ATTRIBUTE, 0);
            getServletContext().getRequestDispatcher(MATCHES_JSP).forward(req, resp);
            return;
        }

        showMatches(req, resp, pageNumber, maybePlayer.get());
    }

    private void showMatches(HttpServletRequest req, HttpServletResponse resp, int pageNumber, Player player)
            throws ServletException, IOException {
        List<Match> matches = findMatchesService.findMatchesByPlayerByPage(player, pageNumber);
        int totalCountOfPages = findMatchesService.getTotalCountOfPagesByPlayer(player);

        req.setAttribute(PAGE_ATTRIBUTE, pageNumber);
        req.setAttribute(MATCHES_ATTRIBUTE, matches);
        req.setAttribute(TOTAL_COUNT_OF_PAGES_ATTRIBUTE, totalCountOfPages);
        getServletContext().getRequestDispatcher(MATCHES_JSP).forward(req, resp);
    }
}
