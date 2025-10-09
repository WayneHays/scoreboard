package com.scoreboard.servlet;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.dto.MatchLiveView;
import com.scoreboard.dto.MatchResult;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.mapper.MatchLiveViewMapper;
import com.scoreboard.mapper.MatchResultMapper;
import com.scoreboard.model.OngoingMatch;
import com.scoreboard.service.ScoreCalculationService;
import com.scoreboard.service.FinishedMatchesService;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.util.WebPaths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreServlet extends HttpServlet {
    private final OngoingMatchesService ongoingMatchesService;
    private final ScoreCalculationService scoreCalculationService;
    private final FinishedMatchesService finishedMatchesService;
    private final MatchLiveViewMapper liveViewMapper;
    private final MatchResultMapper resultMapper;

    public MatchScoreServlet() {
        this.ongoingMatchesService = ApplicationContext.get(OngoingMatchesService.class);
        this.scoreCalculationService = ApplicationContext.get(ScoreCalculationService.class);
        this.finishedMatchesService = ApplicationContext.get(FinishedMatchesService.class);
        this.liveViewMapper = ApplicationContext.get(MatchLiveViewMapper.class);
        this.resultMapper = ApplicationContext.get(MatchResultMapper.class);
    }

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

        scoreCalculationService.awardPointToPlayer(ongoingMatch, pointWinnerId);

        if (ongoingMatch.getWinner() == null) {
            resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + ongoingMatch.getUuid());
        } else {
            MatchResult matchResult = resultMapper.map(ongoingMatch);
            finishedMatchesService.saveToDatabase(ongoingMatch.getMatch());
            ongoingMatchesService.delete(ongoingMatch.getUuid());
            req.setAttribute("matchResult", matchResult);
            getServletContext().getRequestDispatcher(WebPaths.MATCH_RESULT_JSP).forward(req, resp);
        }
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
}
