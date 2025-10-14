package com.scoreboard.servlet;

import com.scoreboard.config.Config;
import com.scoreboard.dto.MatchesPage;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.service.MatchesPageService;
import com.scoreboard.util.WebPaths;
import com.scoreboard.validator.PlayerNameValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/matches")
public class MatchesServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(MatchesServlet.class);
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private MatchesPageService matchesPageServices;
    private int matchesPerPage;

    @Override
    public void init() throws ServletException {
        super.init();
        this.matchesPageServices = getService(MatchesPageService.class);
        Config config = getService(Config.class);
        this.matchesPerPage = config.getInt("matches.per.page");
        logger.debug("MatchesServlet initialized with {} matches per page", matchesPerPage);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String playerName = req.getParameter("filter_by_player_name");
        int pageNumber = parsePageNumber(req.getParameter("page"));

        MatchesPage page = getMatchesPage(playerName, pageNumber);

        req.setAttribute("page", page);
        getServletContext().getRequestDispatcher(WebPaths.MATCHES_JSP).forward(req, resp);
    }

    private MatchesPage getMatchesPage(String playerName, int pageNumber) {
        MatchesPage page;
        if (playerName == null || playerName.isBlank()) {
            logger.debug("Loading all matches page {}", pageNumber);
            page = matchesPageServices.getMatchesPage(pageNumber, matchesPerPage);
        } else {
            try {
                String validName = PlayerNameValidator.validate(playerName);
                logger.debug("Loading matches filtered by player: '{}', page: {}", validName, pageNumber);
                page = matchesPageServices
                        .getMatchesPageByPlayerName(validName, pageNumber, matchesPerPage);
            } catch (ValidationException e) {
                logger.warn("Player name validation failed: '{}' - {}", playerName, e.getMessage());
                page = matchesPageServices
                        .getMatchesPage(pageNumber, matchesPerPage)
                        .withValidationError(e.getMessage(), playerName);
            }
        }
        return page;
    }

    private int parsePageNumber(String pageNumberStr) {
        if (pageNumberStr == null || pageNumberStr.isBlank()) {
            return DEFAULT_PAGE_NUMBER;
        }
        return Integer.parseInt(pageNumberStr);
    }
}