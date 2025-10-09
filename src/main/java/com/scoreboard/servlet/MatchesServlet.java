package com.scoreboard.servlet;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.config.ConfigLoader;
import com.scoreboard.dto.MatchesPage;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.service.MatchesPageService;
import com.scoreboard.util.WebPaths;
import com.scoreboard.validator.PlayerNameValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/matches")
public class MatchesServlet extends HttpServlet {
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int MATCHES_PER_PAGE = ConfigLoader.getInt("pagination.page.size");
    private final MatchesPageService matchesPageService;

    public MatchesServlet() {
        this.matchesPageService = ApplicationContext.get(MatchesPageService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String playerName = req.getParameter("filter_by_player_name");
        int pageNumber = parsePageNumber(req.getParameter("page"));

        MatchesPage page;

        if (playerName == null || playerName.isBlank()) {
            page = matchesPageService.getMatchesPage(pageNumber, MATCHES_PER_PAGE);
        } else {
            try {
                String validName = PlayerNameValidator.validate(playerName);
                page = matchesPageService
                        .getMatchesPageByPlayerName(validName, pageNumber, MATCHES_PER_PAGE);
            } catch (ValidationException e) {
                page = matchesPageService
                        .getMatchesPage(pageNumber, MATCHES_PER_PAGE)
                        .withValidationError(e.getMessage(), playerName);
            }
        }

        req.setAttribute("page", page);
        getServletContext().getRequestDispatcher(WebPaths.MATCHES_JSP).forward(req, resp);
    }

    private int parsePageNumber(String pageNumberStr) {
        if (pageNumberStr == null || pageNumberStr.isBlank()) {
            return DEFAULT_PAGE_NUMBER;
        }
        return Integer.parseInt(pageNumberStr);
    }
}