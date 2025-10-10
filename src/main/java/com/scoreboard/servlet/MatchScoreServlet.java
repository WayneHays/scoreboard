package com.scoreboard.servlet;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.mapper.MatchLiveViewMapper;
import com.scoreboard.mapper.MatchResultMapper;
import com.scoreboard.model.OngoingMatch;
import com.scoreboard.model.entity.Player;
import com.scoreboard.service.FinishedMatchPersistenceService;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.service.ScoreCalculationService;
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
    private final FinishedMatchPersistenceService finishedMatchPersistenceService;
    private final MatchLiveViewMapper liveViewMapper;
    private final MatchResultMapper resultMapper;

    public MatchScoreServlet() {
        this.ongoingMatchesService = ApplicationContext.get(OngoingMatchesService.class);
        this.scoreCalculationService = ApplicationContext.get(ScoreCalculationService.class);
        this.finishedMatchPersistenceService = ApplicationContext.get(FinishedMatchPersistenceService.class);
        this.liveViewMapper = ApplicationContext.get(MatchLiveViewMapper.class);
        this.resultMapper = ApplicationContext.get(MatchResultMapper.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuidStr = req.getParameter("uuid");
        UUID uuid = parseUuid(uuidStr);
        OngoingMatch ongoingMatch = ongoingMatchesService.get(uuid);

        req.setAttribute("matchView", liveViewMapper.map(ongoingMatch));
        getServletContext().getRequestDispatcher(WebPaths.MATCH_SCORE_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String uuidStr = req.getParameter("uuid");
        UUID uuid = parseUuid(uuidStr);
        OngoingMatch ongoingMatch = ongoingMatchesService.get(uuid);

        String playerName = req.getParameter("playerName");
        Player pointWinner = ongoingMatch.getPlayer(playerName);

        scoreCalculationService.winPoint(ongoingMatch, pointWinner);

        if (isWinnerDetermined(ongoingMatch)) {
            finishedMatchPersistenceService.saveFinishedMatch(ongoingMatch);
            ongoingMatchesService.delete(uuid);

            req.setAttribute("matchResult", resultMapper.map(ongoingMatch));
            getServletContext().getRequestDispatcher(WebPaths.MATCH_RESULT_JSP).forward(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + uuid);
        }
    }

    private UUID parseUuid(String uuidStr) {
        if (uuidStr == null || uuidStr.isBlank()) {
            throw new ValidationException("UUID is required");
        }

        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format", e);
        }
    }

    private boolean isWinnerDetermined(OngoingMatch ongoingMatch) {
        return ongoingMatch.getWinner() != null;
    }
}
