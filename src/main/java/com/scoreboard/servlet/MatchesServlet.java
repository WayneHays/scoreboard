package com.scoreboard.servlet;

import com.scoreboard.constant.JspPaths;
import com.scoreboard.dto.response.MatchesPage;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.service.matchespage.MatchesPageService;
import com.scoreboard.util.ServletHelper;
import com.scoreboard.validation.PlayerNameValidator;
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
    private static final String PLAYER_FILTER_PARAM = "filter_by_player_name";
    private static final String PAGE_ATTR = "page";

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
        String playerName = req.getParameter(PLAYER_FILTER_PARAM);
        int pageNumber = ServletHelper.parsePageNumber(req.getParameter(PAGE_ATTR));

        MatchesPage page = getMatchesPage(playerName, pageNumber);

        req.setAttribute(PAGE_ATTR, page);
        getServletContext().getRequestDispatcher(JspPaths.MATCHES_JSP).forward(req, resp);
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
}