package com.scoreboard.servlet;

import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.service.FindMatchesService;
import com.scoreboard.service.PlayerService;
import com.scoreboard.util.JspPaths;
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

@WebServlet("/matches")
public class MatchesServlet extends HttpServlet {
    private static final String PAGE_PARAM = "page";
    private static final String FILTER_BY_PLAYER_NAME_PARAM = "filter_by_player_name";
    private static final String PLAYER_NOT_FOUND_MSG = "Player not found: ";
    private static final String NO_MATCHES_MSG = "No matches found.";
    private static final String PAGE_NOT_FOUND_TEMPLATE = "Page %d not found. Available pages: 1-%d";
    private static final int DEFAULT_PAGE_NUMBER = 1;

    private FindMatchesService findMatchesService = FindMatchesService.getInstance();
    private PlayerService playerService = PlayerService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String playerName = req.getParameter(FILTER_BY_PLAYER_NAME_PARAM);
        String pageNumberStr = req.getParameter(PAGE_PARAM);

        if (playerName == null || playerName.trim().isEmpty()) {
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
            handle404Page(req, resp, pageNumber, totalPages, null);
            return;
        }

        showMatches(req, resp, pageNumber, totalPages, null);
    }

    private void handle404Page(HttpServletRequest req, HttpServletResponse resp,
                               int requestedPage, int totalPages, String playerName)
            throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

        String errorMessage = (totalPages == 0) ? NO_MATCHES_MSG :
                String.format(PAGE_NOT_FOUND_TEMPLATE, requestedPage, totalPages);
        String requestedUrl = buildRequestedUrl(req, requestedPage, playerName);

        RequestAttributeHelper.set404ErrorAttributes(req, errorMessage, requestedUrl);
        getServletContext().getRequestDispatcher(JspPaths.ERROR_404_JSP).forward(req, resp);
    }

    private String buildRequestedUrl(HttpServletRequest req, int requestedPage, String playerName) {
        StringBuilder url = new StringBuilder(req.getRequestURI());
        url.append("?page=").append(requestedPage);
        if (playerName != null && !playerName.trim().isEmpty()) {
            url.append("&filter_by_player_name=").append(playerName);
        }
        return url.toString();
    }

    private void showMatches(HttpServletRequest req, HttpServletResponse resp,
                             int pageNumber, int totalPages, Player player)
            throws ServletException, IOException {
        List<Match> matches = (player == null) ?
                findMatchesService.findMatchesByPage(pageNumber) :
                findMatchesService.findMatchesByPlayerByPage(player, pageNumber);

        RequestAttributeHelper.setMatchesPageAttributes(req, pageNumber, matches, totalPages,
                player != null ? player.getName() : null, null);
        getServletContext().getRequestDispatcher(JspPaths.MATCHES_JSP).forward(req, resp);
    }

    private void handleMatchesByPlayer(HttpServletRequest req, HttpServletResponse resp,
                                       String playerName, String pageNumberStr)
            throws ServletException, IOException {
        Optional<Player> maybePlayer = playerService.find(playerName);

        if (maybePlayer.isEmpty()) {
            String errorMessage = PLAYER_NOT_FOUND_MSG + playerName;
            RequestAttributeHelper.setMatchesPageAttributes(req, DEFAULT_PAGE_NUMBER,
                    new ArrayList<>(), 0, playerName, errorMessage);
            getServletContext().getRequestDispatcher(JspPaths.MATCHES_JSP).forward(req, resp);
            return;
        }

        Player player = maybePlayer.get();
        int totalPages = findMatchesService.getTotalCountOfPagesByPlayer(player);
        int pageNumber = parsePageNumber(pageNumberStr);

        if (isInvalidPage(pageNumber, totalPages)) {
            handle404Page(req, resp, pageNumber, totalPages, playerName);
            return;
        }

        showMatches(req, resp, pageNumber, totalPages, player);
    }

    private int parsePageNumber(String pageNumberStr) {
        try {
            return Integer.parseInt(pageNumberStr);
        } catch (NumberFormatException e) {
            return DEFAULT_PAGE_NUMBER;
        }
    }

    private boolean isInvalidPage(int pageNumber, int totalPages) {
        return pageNumber < 1 || (totalPages > 0 && pageNumber > totalPages);
    }
}