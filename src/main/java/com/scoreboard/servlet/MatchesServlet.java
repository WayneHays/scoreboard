package com.scoreboard.servlet;

import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.service.FindMatchesService;
import com.scoreboard.service.PlayerService;
import com.scoreboard.util.JspPaths;
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

    private static final String PAGE_ATTR = "page";
    private static final String MATCHES_ATTR = "matches";
    private static final String TOTAL_COUNT_OF_PAGES_ATTR = "totalCountOfPages";
    private static final String ERROR_MESSAGE_ATTR = "errorMessage";
    private static final String FILTER_BY_PLAYER_NAME_ATTR = "filter_by_player_name";

    private static final String MATCHES_JSP = JspPaths.MATCHES;
    private static final String ERROR_404_JSP = JspPaths.ERROR_404_JSP;

    private static final String PLAYER_NOT_FOUND_MSG = "Player not found: ";
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final String NO_MATCHES_MSG = "No matches found.";
    private static final String PAGE_NOT_FOUND_FORMAT = "Page %d not found. Available pages: 1-%d";

    private static final String ERROR_CODE_ATTR = "errorCode";
    private static final String REQUESTED_URL_ATTR = "requestedUrl";

    private FindMatchesService findMatchesService = FindMatchesService.getInstance();
    private PlayerService playerService = PlayerService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String playerName = req.getParameter(FILTER_BY_PLAYER_NAME_PARAM);
        String pageNumberStr = req.getParameter(PAGE_PARAM);

        if (isPlayerFilterEmpty(playerName)) {
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

        showAllMatches(req, resp, pageNumber, totalPages);
    }

    private void handleMatchesByPlayer(HttpServletRequest req, HttpServletResponse resp,
                                       String playerName, String pageNumberStr)
            throws ServletException, IOException {
        Optional<Player> maybePlayer = playerService.find(playerName);

        if (maybePlayer.isEmpty()) {
            handlePlayerNotFound(req, resp, playerName);
            return;
        }

        Player player = maybePlayer.get();
        int totalPages = findMatchesService.getTotalCountOfPagesByPlayer(player);
        int pageNumber = parsePageNumber(pageNumberStr);

        if (isInvalidPage(pageNumber, totalPages)) {
            handle404Page(req, resp, pageNumber, totalPages, playerName);
            return;
        }

        showMatchesByPlayer(req, resp, player, pageNumber, totalPages);
    }

    private boolean isPlayerFilterEmpty(String playerName) {
        return playerName == null || playerName.trim().isEmpty();
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

    private void handle404Page(HttpServletRequest req, HttpServletResponse resp,
                               int requestedPage, int totalPages, String playerName)
            throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

        String errorMessage = buildPageNotFoundMessage(requestedPage, totalPages);

        req.setAttribute(ERROR_CODE_ATTR, 404);
        req.setAttribute(ERROR_MESSAGE_ATTR, errorMessage);
        req.setAttribute(REQUESTED_URL_ATTR, buildRequestedUrl(req, requestedPage, playerName));

        getServletContext().getRequestDispatcher(ERROR_404_JSP).forward(req, resp);
    }

    private String buildPageNotFoundMessage(int requestedPage, int totalPages) {
        if (totalPages == 0) {
            return NO_MATCHES_MSG;
        }
        return String.format(PAGE_NOT_FOUND_FORMAT, requestedPage, totalPages);
    }

    private String buildRequestedUrl(HttpServletRequest req, int requestedPage, String playerName) {
        StringBuilder url = new StringBuilder(req.getRequestURI());
        url.append("?page=").append(requestedPage);
        if (playerName != null && !playerName.trim().isEmpty()) {
            url.append("&filter_by_player_name=").append(playerName);
        }
        return url.toString();
    }

    private void showAllMatches(HttpServletRequest req, HttpServletResponse resp, int pageNumber, int totalPages)
            throws ServletException, IOException {
        List<Match> matches = findMatchesService.findMatchesByPage(pageNumber);
        forwardToMatchesPage(req, resp, pageNumber, matches, totalPages, null);
    }

    private void showMatchesByPlayer(HttpServletRequest req, HttpServletResponse resp,
                                     Player player, int pageNumber, int totalPages)
            throws ServletException, IOException {
        List<Match> matches = findMatchesService.findMatchesByPlayerByPage(player, pageNumber);
        forwardToMatchesPage(req, resp, pageNumber, matches, totalPages, player.getName());
    }

    private void handlePlayerNotFound(HttpServletRequest req, HttpServletResponse resp, String playerName)
            throws ServletException, IOException {
        String errorMessage = PLAYER_NOT_FOUND_MSG + playerName;
        forwardToMatchesPageWithError(req, resp, DEFAULT_PAGE_NUMBER, new ArrayList<>(), 0, playerName, errorMessage);
    }

    private void forwardToMatchesPage(HttpServletRequest req, HttpServletResponse resp,
                                      int pageNumber, List<Match> matches, int totalPages,
                                      String filterPlayerName) throws ServletException, IOException {
        forwardToMatchesPageWithError(req, resp, pageNumber, matches, totalPages, filterPlayerName, null);
    }

    private void forwardToMatchesPageWithError(HttpServletRequest req, HttpServletResponse resp,
                                               int pageNumber, List<Match> matches, int totalPages,
                                               String filterPlayerName, String errorMessage)
            throws ServletException, IOException {

        req.setAttribute(PAGE_ATTR, pageNumber);
        req.setAttribute(MATCHES_ATTR, matches);
        req.setAttribute(TOTAL_COUNT_OF_PAGES_ATTR, totalPages);

        if (filterPlayerName != null) {
            req.setAttribute(FILTER_BY_PLAYER_NAME_ATTR, filterPlayerName);
        }

        if (errorMessage != null) {
            req.setAttribute(ERROR_MESSAGE_ATTR, errorMessage);
        }

        getServletContext().getRequestDispatcher(MATCHES_JSP).forward(req, resp);
    }
}