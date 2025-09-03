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
    public static final String PAGE = "page";
    public static final String FILTER_BY_PLAYER_NAME = "filter_by_player_name";
    public static final String MATCHES_JSP = "/WEB-INF/matches.jsp";
    public static final String MATCHES = "matches";
    public static final String TOTAL_COUNT_OF_PAGES = "totalCountOfPages";

    private FindMatchesService findMatchesService = FindMatchesService.getInstance();
    private PlayerService playerService = PlayerService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String playerName = req.getParameter(FILTER_BY_PLAYER_NAME);

        if (playerName == null) {
            showAllMatchesByPage(req, resp);
        } else {
            showPlayerMatchesByPage(req, resp);
        }
    }

    private void showAllMatchesByPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int pageNumber = parsePageNumber(req);
        List<Match> matches = findMatchesService.findMatchesByPage(pageNumber);
        int totalCountOfPages = findMatchesService.getTotalCountOfPages();

        req.setAttribute(PAGE, pageNumber);
        req.setAttribute(MATCHES, matches);
        req.setAttribute(TOTAL_COUNT_OF_PAGES, totalCountOfPages);
        getServletContext().getRequestDispatcher(MATCHES_JSP).forward(req, resp);
    }

    private void showPlayerMatchesByPage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int pageNumber = parsePageNumber(req);
        String playerName = req.getParameter(FILTER_BY_PLAYER_NAME);
        if (playerName == null || playerName.trim().isEmpty()) {
            showAllMatchesByPage(req, resp);
            return;
        }

        Optional<Player> maybePlayer = playerService.find(playerName);
        if (maybePlayer.isEmpty()) {
            req.setAttribute("errorMessage", "Matches not found with player: " + playerName);
            req.setAttribute(MATCHES, new ArrayList<>());
            req.setAttribute(TOTAL_COUNT_OF_PAGES, 0);
            getServletContext().getRequestDispatcher(MATCHES_JSP).forward(req, resp);
            return;
        }

        showPlayerMatches(req, resp, pageNumber, maybePlayer.get());
    }

    private void showPlayerMatches(HttpServletRequest req, HttpServletResponse resp, int pageNumber, Player player)
            throws ServletException, IOException {
        List<Match> matches = findMatchesService.findMatchesByPlayerByPage(player, pageNumber);
        int totalCountOfPages = findMatchesService.getTotalCountOfPagesByPlayer(player);

        req.setAttribute("playerName", player);
        req.setAttribute(PAGE, pageNumber);
        req.setAttribute(MATCHES, matches);
        req.setAttribute(TOTAL_COUNT_OF_PAGES, totalCountOfPages);

        getServletContext().getRequestDispatcher(MATCHES_JSP).forward(req, resp);
    }

    private int parsePageNumber(HttpServletRequest req) {
        String pageNumberStr = req.getParameter(PAGE);
        if (pageNumberStr == null || pageNumberStr.isEmpty()) {
            return 1;
        }
        try {
            return Integer.parseInt(pageNumberStr);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
