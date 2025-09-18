package com.scoreboard.servlet;

import com.scoreboard.dto.MatchLiveView;
import com.scoreboard.dto.MatchResult;
import com.scoreboard.dto.OngoingMatch;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.service.FinishedMatchService;
import com.scoreboard.service.MatchGameplayService;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.mapper.MatchLiveViewMapper;
import com.scoreboard.mapper.MatchResultMapper;
import com.scoreboard.util.WebPaths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet(WebPaths.MATCH_SCORE_URL)
public class MatchScoreServlet extends HttpServlet {
    private final OngoingMatchesService ongoingMatchesService = OngoingMatchesService.getInstance();
    private final MatchGameplayService matchGameplayService = MatchGameplayService.getInstance();
    private final FinishedMatchService finishedMatchService = FinishedMatchService.getInstance();
    private final MatchLiveViewMapper liveViewMapper = new MatchLiveViewMapper();
    private final MatchResultMapper resultMapper = new MatchResultMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OngoingMatch ongoingMatch = findMatch(req);
        MatchLiveView matchView = liveViewMapper.map(ongoingMatch);
        req.setAttribute("matchView", matchView);
        getServletContext().getRequestDispatcher(WebPaths.MATCH_SCORE_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        OngoingMatch ongoingMatch = findMatch(req);
        String pointWinnerId = req.getParameter("pointWinnerId");
        processPointWin(req, resp, ongoingMatch, pointWinnerId);
    }

    private OngoingMatch findMatch(HttpServletRequest req) {
        String uuidStr = req.getParameter("uuid");
        UUID uuid = parseUuid(uuidStr);
        return ongoingMatchesService.get(uuid);
    }

    private UUID parseUuid(String uuidStr) {
        if (uuidStr == null || uuidStr.isBlank()) {
            throw new ValidationException("UUID is required");
        }

        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid UUID format");
        }
    }

    private void processPointWin(
            HttpServletRequest req,
            HttpServletResponse resp,
            OngoingMatch ongoingMatch,
            String pointWinnerId) throws IOException, ServletException {

        matchGameplayService.processPoint(ongoingMatch, pointWinnerId);

        if (matchGameplayService.isMatchFinished(ongoingMatch)) {
            matchGameplayService.setMatchWinner(ongoingMatch);

            MatchResult result = resultMapper.map(ongoingMatch);
            finishedMatchService.saveToDatabase(ongoingMatch.getMatch());
            ongoingMatchesService.delete(ongoingMatch.getUuid());

            showMatchResult(req, resp, result);
        } else {
            continueMatch(req, resp, ongoingMatch);
        }
    }

    private void showMatchResult(HttpServletRequest req, HttpServletResponse resp, MatchResult matchResult)
            throws ServletException, IOException {
        req.setAttribute("matchResult", matchResult);
        getServletContext().getRequestDispatcher(WebPaths.MATCH_RESULT_JSP).forward(req, resp);
    }

    private void continueMatch(HttpServletRequest req, HttpServletResponse resp,
                               OngoingMatch ongoingMatch)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + ongoingMatch.getUuid());
    }
}
