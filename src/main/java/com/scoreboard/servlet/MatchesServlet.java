package com.scoreboard.servlet;

import com.scoreboard.dto.MatchesPageDto;
import com.scoreboard.service.MatchesPageService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@WebServlet("/matches")
@Slf4j
public class MatchesServlet extends BaseServlet {
    private static final String JSP = "matches";
    private static final String ATTR_PAGE = "page";
    private static final String PARAM_PLAYER_NAME = "playerName";
    private static final String MSG_INVALID_PAGE = "Invalid page number: must be digit > 0, got '%s'";

    private static final int DEFAULT_MATCHES_PER_PAGE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 1;

    private MatchesPageService matchesPageService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.matchesPageService = getService(MatchesPageService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String playerName = req.getParameter(PARAM_PLAYER_NAME);
        int pageNumber = resolve(req.getParameter(ATTR_PAGE));

        MatchesPageDto matchesPage = matchesPageService.getMatchesPage(playerName, pageNumber, DEFAULT_MATCHES_PER_PAGE);

        req.setAttribute(PARAM_PLAYER_NAME, playerName);
        req.setAttribute(ATTR_PAGE, matchesPage);
        forwardTo(JSP, req, resp);
    }

    private int resolve(String pageNumberStr) {
        if (StringUtils.isBlank(pageNumberStr)) {
            return DEFAULT_PAGE_NUMBER;
        }

        int pageNumber = Integer.parseInt(pageNumberStr);

        if (pageNumber < 0) {
            throw new NumberFormatException(MSG_INVALID_PAGE.formatted(pageNumber));
        }

        return pageNumber;
    }
}