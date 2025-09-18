package com.scoreboard.servlet;

import com.scoreboard.dto.FinishedMatchesPage;
import com.scoreboard.service.MatchesPageService;
import com.scoreboard.util.WebPaths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(WebPaths.MATCHES_URL)
public class MatchesServlet extends HttpServlet {
    private static final int DEFAULT_PAGE_NUMBER = 1;

    private final MatchesPageService pageService = MatchesPageService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String playerName = req.getParameter("filter_by_player_name");
        String pageNumberStr = req.getParameter("page");
        int pageNumber = parsePageNumber(pageNumberStr);

        FinishedMatchesPage finishedMatchesPage = (playerName == null || playerName.isBlank()) ?
                pageService.getAllMatchesPage(pageNumber) :
                pageService.getPlayerMatchesPage(playerName, pageNumber);

        req.setAttribute("matchesPage", finishedMatchesPage);
        getServletContext().getRequestDispatcher(WebPaths.MATCHES_JSP).forward(req, resp);
    }

    private int parsePageNumber(String pageNumberStr) {
        if (pageNumberStr == null || pageNumberStr.isBlank()) {
            return DEFAULT_PAGE_NUMBER;
        }
        return Integer.parseInt(pageNumberStr);
    }
}