package com.scoreboard.servlet;

import com.scoreboard.dto.MatchesPage;
import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.service.FindMatchesService;
import com.scoreboard.service.PlayerService;
import com.scoreboard.util.ErrorHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
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
        int pageNumber = parsePageNumber(pageNumberStr);

        MatchesPage matchesPage = (playerName == null || playerName.isBlank()) ?
                buildAllMatchesPage(pageNumber) :
                buildPlayerMatchesPage(playerName, pageNumber);

        if (matchesPage.hasError() && shouldReturn404(matchesPage)) {
            ErrorHandler.handleHttpError(req, resp, SC_NOT_FOUND, matchesPage.errorMessage());
            return;
        }

        req.setAttribute("matchesPage", matchesPage);
        getServletContext().getRequestDispatcher(MATCHES_JSP).forward(req, resp);
    }

    private int parsePageNumber(String pageNumberStr) {
        if (pageNumberStr == null || pageNumberStr.isBlank()) {
            return DEFAULT_PAGE_NUMBER;
        }
        return Integer.parseInt(pageNumberStr);
    }

    private MatchesPage buildAllMatchesPage(int pageNumber) {
        int totalPages = findMatchesService.getTotalCountOfPages();

        if (isInvalidPage(pageNumber, totalPages)) {
            String error = (totalPages == 0) ? "No matches found" :
                    String.format("Page %d not found. Available pages: 1-%d", pageNumber, totalPages);
            return new MatchesPage(pageNumber, List.of(), totalPages, null, error);
        }

        List<Match> matches = findMatchesService.findMatchesByPage(pageNumber);
        return new MatchesPage(pageNumber, matches, totalPages);
    }

    private MatchesPage buildPlayerMatchesPage(String playerName, int pageNumber) {
        Optional<Player> maybePlayer = playerService.find(playerName);

        if (maybePlayer.isEmpty()) {
            return new MatchesPage(DEFAULT_PAGE_NUMBER, List.of(), 0,
                    playerName, "Player not found: " + playerName);
        }

        Player player = maybePlayer.get();
        int totalPages = findMatchesService.getTotalCountOfPagesByPlayer(player);

        if (isInvalidPage(pageNumber, totalPages)) {
            String error = String.format("Page %d not found. Available pages: 1-%d",
                    pageNumber, totalPages);
            return new MatchesPage(pageNumber, List.of(), totalPages, playerName, error);
        }

        List<Match> matches = findMatchesService.findMatchesByPlayerByPage(player, pageNumber);
        return new MatchesPage(pageNumber, matches, totalPages, playerName);
    }

    private boolean shouldReturn404(MatchesPage page) {
        return page.errorMessage().contains("Page") && page.errorMessage().contains("not found");
    }

    private boolean isInvalidPage(int pageNumber, int totalPages) {
        return pageNumber < 1 || (totalPages > 0 && pageNumber > totalPages);
    }
}