package com.scoreboard.servlet;

import com.scoreboard.dto.response.MatchesPage;
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
    private static final int DEFAULT_MATCHES_PER_PAGE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final String JSP_NAME = "matches";
    private static final String PLAYER_FILTER_PARAM = "playerName";
    private static final String PAGE_ATTR = "page";

    private MatchesPageService matchesPageService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.matchesPageService = getService(MatchesPageService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String playerName = req.getParameter(PLAYER_FILTER_PARAM);
        int pageNumber = resolve(req.getParameter(PAGE_ATTR));

        MatchesPage matchesPage = matchesPageService.getPage(playerName, pageNumber, DEFAULT_MATCHES_PER_PAGE);
        req.setAttribute(PAGE_ATTR, matchesPage);
        forwardTo(JSP_NAME, req, resp);
    }

    private int resolve(String pageNumberStr) {
        if (StringUtils.isBlank(pageNumberStr)) {
            return DEFAULT_PAGE_NUMBER;
        }

        int pageNumber = Integer.parseInt(pageNumberStr);

        if (pageNumber < 0) {
            throw new NumberFormatException("Invalid page number: must be digit > 0, got '%s'".formatted(pageNumber));
        }

        return pageNumber;
    }
}