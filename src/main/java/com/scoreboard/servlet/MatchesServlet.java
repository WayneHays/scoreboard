package com.scoreboard.servlet;

import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.service.FindMatchesService;
import com.scoreboard.service.PlayerService;
import com.scoreboard.util.ErrorHandler;
import com.scoreboard.util.RequestAttributeHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@WebServlet("/matches")
public class MatchesServlet extends HttpServlet {
    private static final String MATCHES_JSP = "/WEB-INF/matches.jsp";
    private static final int DEFAULT_PAGE_NUMBER = 1;

    private final FindMatchesService findMatchesService = FindMatchesService.getInstance();
    private final PlayerService playerService = PlayerService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String playerName = req.getParameter("filter_by_player_name");
        String pageNumberStr = req.getParameter("page");

        if (playerName == null || playerName.isBlank()) {
            handleAllMatches(req, resp, pageNumberStr);
        } else {
            handleMatchesByPlayer(req, resp, playerName, pageNumberStr);
        }
    }

    private void handleAllMatches(HttpServletRequest req, HttpServletResponse resp, String pageNumberStr)
            throws ServletException, IOException {
        int totalPages = findMatchesService.getTotalCountOfPages();
        int pageNumber = parsePageNumber(pageNumberStr);

        if (isInvalidPage(pageNumber, totalPages)) {
            handlePageNotFound(req, resp, pageNumber, totalPages);
            return;
        }

        showMatches(req, resp, pageNumber, totalPages, null);
    }

    private void showMatches(HttpServletRequest req, HttpServletResponse resp,
                             int pageNumber, int totalPages, Player player) throws ServletException, IOException {
        List<Match> matches = (player == null) ?
                findMatchesService.findMatchesByPage(pageNumber) :
                findMatchesService.findMatchesByPlayerByPage(player, pageNumber);

        RequestAttributeHelper.setMatchesPageAttributes(
                req,
                pageNumber,
                matches,
                totalPages,
                player != null ? player.getName() : null,
                null);
        getServletContext().getRequestDispatcher(MATCHES_JSP).forward(req, resp);
    }

    private void handleMatchesByPlayer(HttpServletRequest req, HttpServletResponse resp,
                                       String playerName, String pageNumberStr)
            throws ServletException, IOException {
        Optional<Player> maybePlayer = playerService.find(playerName);

        if (maybePlayer.isEmpty()) {
            String errorMessage = "Player not found: " + playerName;
            RequestAttributeHelper.setMatchesPageAttributes(
                    req,
                    DEFAULT_PAGE_NUMBER,
                    new ArrayList<>(),
                    0,
                    playerName,
                    errorMessage);
            getServletContext().getRequestDispatcher(MATCHES_JSP).forward(req, resp);
            return;
        }

        Player player = maybePlayer.get();
        int totalPages = findMatchesService.getTotalCountOfPagesByPlayer(player);
        int pageNumber = parsePageNumber(pageNumberStr);

        if (isInvalidPage(pageNumber, totalPages)) {
            handlePageNotFound(req, resp, pageNumber, totalPages);
            return;
        }

        showMatches(req, resp, pageNumber, totalPages, player);
    }

    private int parsePageNumber(String pageNumberStr) {
        if (pageNumberStr == null || pageNumberStr.isBlank()) {
            return DEFAULT_PAGE_NUMBER;
        }
        return Integer.parseInt(pageNumberStr);
    }

    private boolean isInvalidPage(int pageNumber, int totalPages) {
        return pageNumber < 1 || (totalPages > 0 && pageNumber > totalPages);
    }

    private void handlePageNotFound(HttpServletRequest req, HttpServletResponse resp,
                                    int requestedPage, int totalPages)
            throws ServletException, IOException {
        String errorMessage = (totalPages == 0) ? "No matches found"  :
                String.format("Page %d not found. Available pages: 1-%d", requestedPage, totalPages);

        ErrorHandler.handleHttpError(req, resp, SC_NOT_FOUND, errorMessage);
    }
}