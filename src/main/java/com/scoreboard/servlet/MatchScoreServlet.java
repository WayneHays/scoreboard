package com.scoreboard.servlet;

import com.scoreboard.dto.OngoingMatch;
import com.scoreboard.model.Player;
import com.scoreboard.service.FinishedMatchService;
import com.scoreboard.service.MatchGameplayService;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.util.JspPaths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreServlet extends HttpServlet {
    private static final int REQUIRED_UUID_LENGTH = 36;

    private final OngoingMatchesService ongoingMatchesService = OngoingMatchesService.getInstance();
    private final MatchGameplayService matchGameplayService = MatchGameplayService.getInstance();
    private final FinishedMatchService finishedMatchService = FinishedMatchService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OngoingMatch ongoingMatch = findMatch(req);
        req.setAttribute("ongoingMatch", ongoingMatch);
        getServletContext().getRequestDispatcher(JspPaths.MATCH_SCORE_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OngoingMatch ongoingMatch = findMatch(req);
        String pointWinnerId = req.getParameter("pointWinnerId");

        if (pointWinnerId == null || pointWinnerId.isBlank()) {
            throw new IllegalArgumentException("Player ID is required");
        }

        processPointWin(req, resp, ongoingMatch, pointWinnerId);
    }

    private OngoingMatch findMatch(HttpServletRequest req) {
        String uuidStr = req.getParameter("uuid");
        UUID uuid = parseUuid(uuidStr);
        OngoingMatch ongoingMatch = ongoingMatchesService.get(uuid);

        if (ongoingMatch == null) {
            throw new IllegalArgumentException("Match not found");
        }

        return ongoingMatch;
    }

    private UUID parseUuid(String uuidStr) {
        if (uuidStr == null || uuidStr.isBlank()) {
            throw new IllegalArgumentException("UUID is required");
        }

        if (uuidStr.length() != REQUIRED_UUID_LENGTH) {
            throw new IllegalArgumentException("UUID must be 36 characters long");
        }

        return UUID.fromString(uuidStr);
    }

    private void processPointWin(
            HttpServletRequest req,
            HttpServletResponse resp,
            OngoingMatch ongoingMatch,
            String pointWinnerId) throws ServletException, IOException {

        matchGameplayService.processPoint(ongoingMatch, pointWinnerId);

        if (matchGameplayService.isMatchFinished(ongoingMatch)) {
            Player winner = matchGameplayService.getMatchWinner(ongoingMatch);
            handleMatchCompletion(req, resp, ongoingMatch, winner);
        } else {
            continueMatch(resp, ongoingMatch);
        }
    }

    private void handleMatchCompletion(
            HttpServletRequest req,
            HttpServletResponse resp,
            OngoingMatch ongoingMatch,
            Player winner) throws ServletException, IOException {

        ongoingMatch.getMatch().setWinner(winner);
        finishedMatchService.saveToDatabase(ongoingMatch.getMatch());
        ongoingMatchesService.delete(ongoingMatch.getUuid());

        req.setAttribute("ongoingMatch", ongoingMatch);
        getServletContext().getRequestDispatcher(JspPaths.MATCH_RESULT_JSP).forward(req, resp);
    }


    private void continueMatch(HttpServletResponse resp,
                               OngoingMatch ongoingMatch)
            throws IOException {
        resp.sendRedirect("/match-score?uuid=" + ongoingMatch.getUuid());
    }
}
