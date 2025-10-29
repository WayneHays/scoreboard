package com.scoreboard.servlet;

import com.scoreboard.constant.WebPaths;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.mapper.MatchLiveViewMapper;
import com.scoreboard.mapper.MatchResultMapper;
import com.scoreboard.model.entity.Player;
import com.scoreboard.model.ongoingmatch.OngoingMatch;
import com.scoreboard.service.finishedmatchpersistenceservice.FinishedMatchPersistenceService;
import com.scoreboard.service.ongoingmatchesservice.OngoingMatchesService;
import com.scoreboard.service.scorecalculation.ScoreCalculationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/match-score")
public class MatchScoreServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(MatchScoreServlet.class);

    private OngoingMatchesService ongoingMatchesService;
    private ScoreCalculationService scoreCalculationService;
    private FinishedMatchPersistenceService finishedMatchPersistenceService;
    private MatchLiveViewMapper liveViewMapper;
    private MatchResultMapper resultMapper;

    private final ConcurrentHashMap<UUID, Object> matchLocks = new ConcurrentHashMap<>();

    @Override
    public void init() throws ServletException {
        super.init();
        this.ongoingMatchesService = getService(OngoingMatchesService.class);
        this.scoreCalculationService = getService(ScoreCalculationService.class);
        this.finishedMatchPersistenceService = getService(FinishedMatchPersistenceService.class);
        this.liveViewMapper = new MatchLiveViewMapper();
        this.resultMapper = new MatchResultMapper();

        logger.info("MatchScoreServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID uuid = parseUuid(req.getParameter("uuid"));
        OngoingMatch ongoingMatch = ongoingMatchesService.get(uuid);

        logger.debug("Displaying match: UUID={}, players='{}' vs '{}'",
                uuid, ongoingMatch.getPlayer1().getName(), ongoingMatch.getPlayer2().getName());

        req.setAttribute("matchView", liveViewMapper.map(ongoingMatch));
        getServletContext().getRequestDispatcher(WebPaths.MATCH_SCORE_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        UUID uuid = parseUuid(req.getParameter("uuid"));
        Object matchLock = matchLocks.computeIfAbsent(uuid, k -> new Object());

        synchronized (matchLock) {
            OngoingMatch ongoingMatch = ongoingMatchesService.get(uuid);
            String playerName = req.getParameter("playerName");
            Player scorer = ongoingMatch.getPlayerByName(playerName);

            scoreCalculationService.awardPoint(ongoingMatch, scorer);
            logger.debug("Point awarded to player: {}", playerName);

            if (ongoingMatch.isFinished()) {
                handleMatchCompletion(req, resp, ongoingMatch, uuid);
            } else {
                resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + uuid);
            }
        }
    }

    private void handleMatchCompletion(HttpServletRequest req, HttpServletResponse resp, OngoingMatch ongoingMatch, UUID uuid) throws IOException {
        Player winner = ongoingMatch.getWinner();
        logger.info("Match completed - UUID: {}, Winner: {}", uuid, winner.getName());

        finishedMatchPersistenceService.saveFinishedMatch(ongoingMatch);
        HttpSession session = req.getSession();
        session.setAttribute("matchResult", resultMapper.map(ongoingMatch));

        ongoingMatchesService.delete(uuid);
        matchLocks.remove(uuid);

        resp.sendRedirect(req.getContextPath() + "/match-result");
    }

    private UUID parseUuid(String uuidStr) {
        if (uuidStr == null || uuidStr.isBlank()) {
            logger.warn("UUID parameter is missing or empty");
            throw new ValidationException("UUID is required");
        }

        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid UUID format", e);
        }
    }
}
