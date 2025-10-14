package com.scoreboard.servlet;

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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(MatchScoreServlet.class);
    private OngoingMatchesService ongoingMatchesService;
    private ScoreCalculationService scoreCalculationService;
    private FinishedMatchPersistenceService finishedMatchPersistenceService;
    private MatchLiveViewMapper liveViewMapper;
    private MatchResultMapper resultMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ongoingMatchesService = getService(OngoingMatchesService.class);
        this.scoreCalculationService = getService(ScoreCalculationService.class);
        this.finishedMatchPersistenceService = getService(FinishedMatchPersistenceService.class);
        this.liveViewMapper = getService(MatchLiveViewMapper.class);
        this.resultMapper = getService(MatchResultMapper.class);

        logger.debug("MatchScoreServlet dependencies initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuidStr = req.getParameter("uuid");
        UUID uuid = parseUuid(uuidStr);
        OngoingMatch ongoingMatch = ongoingMatchesService.get(uuid);

        req.setAttribute("matchView", liveViewMapper.map(ongoingMatch));
        getServletContext().getRequestDispatcher(WebPaths.MATCH_SCORE_JSP).forward(req, resp);
        logger.debug("Match score page rendered - Players: {} vs {}",
                ongoingMatch.getFirstPlayer().getName(), ongoingMatch.getSecondPlayer().getName());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String uuidStr = req.getParameter("uuid");
        UUID uuid = parseUuid(uuidStr);
        OngoingMatch ongoingMatch = ongoingMatchesService.get(uuid);

        String playerName = req.getParameter("playerName");
        Player pointWinner = ongoingMatch.getPlayer(playerName);

        scoreCalculationService.winPoint(ongoingMatch, pointWinner);
        logger.debug("Point awarded to player: {}", playerName);

        if (isWinnerDetermined(ongoingMatch)) {
            logger.info("Match completed - UUID: {}, Winner: {}", uuid, ongoingMatch.getWinner().getName());
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
            logger.warn("UUID parameter is missing or empty");
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
