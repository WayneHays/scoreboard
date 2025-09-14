package com.scoreboard.servlet;

import com.scoreboard.model.MatchWithScore;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;
import com.scoreboard.service.FinishedMatchService;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.service.ScoreCalculationService;
import com.scoreboard.util.JspPaths;
import com.scoreboard.util.RequestAttributeHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreServlet extends HttpServlet {
    private static final String UUID_PARAM = "uuid";
    private static final String PLAYER_WON_POINT_ID_PARAM = "playerWonPointId";
    private static final String MATCH_UUID_REDIRECT_LINK = "/match-score?uuid=";

    private OngoingMatchesService ongoingMatchesService = OngoingMatchesService.getInstance();
    private ScoreCalculationService scoreCalculationService = ScoreCalculationService.getInstance();
    private FinishedMatchService finishedMatchService = FinishedMatchService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuidStr = req.getParameter(UUID_PARAM);
        UUID uuid = parseUuidOrHandleError(req, resp, uuidStr);
        MatchWithScore matchWithScore = ongoingMatchesService.find(uuid);

        if (matchWithScore == null) {
            handleMatchError(req, resp, UuidErrorType.MATCH_NOT_FOUND);
            return;
        }
        RequestAttributeHelper.setOngoingMatchAttributes(req, matchWithScore, uuid);
        getServletContext().getRequestDispatcher(JspPaths.MATCH_SCORE_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuidStr = req.getParameter(UUID_PARAM);
        String playerWonPointId = req.getParameter(PLAYER_WON_POINT_ID_PARAM);
        UUID uuid = parseUuidOrHandleError(req, resp, uuidStr);
        MatchWithScore matchWithScore = ongoingMatchesService.find(uuid);
        Player firstPlayer = matchWithScore.match().getFirstPlayer();
        Player secondPlayer = matchWithScore.match().getSecondPlayer();
        Player pointWinner = playerWonPointId.equals(firstPlayer.getId().toString()) ? firstPlayer : secondPlayer;
        Score calculatedScore = scoreCalculationService.calculate(matchWithScore, pointWinner);

        if (calculatedScore.isMatchFinished()) {
            ongoingMatchesService.delete(uuid);
            matchWithScore.match().setWinner(pointWinner);
            finishedMatchService.saveToDatabase(matchWithScore.match());
            RequestAttributeHelper.setFinishedMatchAttributes(req, matchWithScore, pointWinner);
            getServletContext().getRequestDispatcher(JspPaths.MATCH_RESULT_JSP).forward(req, resp);
        } else {
            resp.sendRedirect(MATCH_UUID_REDIRECT_LINK + uuid);
        }
    }

    private UUID parseUuidOrHandleError(HttpServletRequest req, HttpServletResponse resp, String uuidStr)
            throws ServletException, IOException {
        if (uuidStr == null || uuidStr.isBlank()) {
            handleMatchError(req, resp, UuidErrorType.MATCH_ID_REQUIRED);
            return null;
        }

        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            handleMatchError(req, resp, UuidErrorType.INVALID_FORMAT);
            return null;
        }
    }

    private void handleMatchError(HttpServletRequest req, HttpServletResponse resp, UuidErrorType errorType)
            throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        String requestedUrl = buildUrl(req);
        RequestAttributeHelper.setMatchErrorAttributes(req, errorType, requestedUrl);
        getServletContext().getRequestDispatcher(JspPaths.MATCH_ERROR_JSP).forward(req, resp);
    }

    private String buildUrl(HttpServletRequest req) {
        return req.getRequestURI() +
               (req.getQueryString() != null ? "?" + req.getQueryString() : "");
    }
}
