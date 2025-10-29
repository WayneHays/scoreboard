package com.scoreboard.servlet;

import com.scoreboard.constant.AppDefaults;
import com.scoreboard.constant.WebPaths;
import com.scoreboard.dto.response.MatchesPage;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.service.matchespageservice.MatchesPageService;
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

    private MatchesPageService matchesPageService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.matchesPageService = getService(MatchesPageService.class);
        logger.info("MatchesServlet initialized");
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
        if (playerName == null || playerName.isBlank()) {
            logger.debug("Loading matches page {}", pageNumber);
            return matchesPageService.getPage(pageNumber);
        }

        try {
            String validName = PlayerNameValidator.validate(playerName);
            logger.debug("Loading matches filtered by '{}', page {}", validName, pageNumber);
            return matchesPageService.getPageByPlayerName(validName, pageNumber);

        } catch (ValidationException e) {
            logger.debug("Validation failed for player name '{}': {}", playerName, e.getMessage());
            return matchesPageService
                    .getPage(pageNumber)
                    .withValidationError(e.getMessage(), playerName);
        }
    }

    private int parsePageNumber(String pageNumberStr) {
        if (pageNumberStr == null || pageNumberStr.isBlank()) {
            return AppDefaults.DEFAULT_PAGE_NUMBER;
        }

        try {
            return Integer.parseInt(pageNumberStr);
        } catch (NumberFormatException e) {
            logger.warn("Invalid page number format: '{}'", pageNumberStr);
            throw new ValidationException("Invalid page number: " + pageNumberStr, e);
        }
    }
}