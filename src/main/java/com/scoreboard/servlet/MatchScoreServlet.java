package com.scoreboard.servlet;

import com.scoreboard.model.MatchWithScore;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;
import com.scoreboard.service.FinishedMatchService;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.service.ScoreCalculationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreServlet extends HttpServlet {
    private static final String MATCH_SCORE_JSP = "/WEB-INF/match-score.jsp";
    private static final String MATCH_RESULT_JSP = "/WEB-INF/match-result.jsp";
    private static final String MATCH_ERROR_JSP = "/WEB-INF/error/match-error.jsp";

    public static final String UUID_PARAM = "uuid";
    public static final String PLAYER_WON_POINT_ID_PARAM = "playerWonPointId";

    public static final String MATCH_WITH_SCORE_ATTR = "matchWithScore";
    public static final String CURRENT_SCORE_ATTR = "currentScore";
    public static final String PLAYER_1_ATTR = "player1";
    public static final String PLAYER_2_ATTR = "player2";
    private static final String UUID_ATTR = "uuid";
    public static final String POINT_WINNER_ATTR = "winner";
    public static final String PLAYER_1_SETS_ATTR = "player1sets";
    public static final String PLAYER_2_SETS_ATTR = "player2sets";
    public static final String ERROR_TITLE_ATTR = "errorTitle";
    public static final String ERROR_DESCRIPTION_ATTR = "errorDescription";
    public static final String REQUESTED_URL_ATTR = "requestedUrl";

    public static final String MATCH_ID_REQUIRED_TITLE = "Match ID is required";
    public static final String MATCH_ID_REQUIRED_DESCRIPTION = "Please provide a valid match ID in the URL.";
    public static final String INVALID_FORMAT_TITLE = "Invalid match ID format";
    public static final String INVALID_FORMAT_DESCRIPTION = "The provided match ID is not in the correct format.";
    public static final String MATCH_NOT_FOUND_TITLE = "Match not found";
    public static final String MATCH_NOT_FOUND_DESCRIPTION = "No active match found with this ID. The match may have ended or the ID is incorrect.";
    public static final String MATCH_SCORE_REDIRECT_LINK = "/match-score?uuid=";

    private OngoingMatchesService ongoingMatchesService = OngoingMatchesService.getInstance();
    private ScoreCalculationService scoreCalculationService = ScoreCalculationService.getInstance();
    private FinishedMatchService finishedMatchService = FinishedMatchService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuidStr = req.getParameter(UUID_PARAM);

        if (uuidStr == null || uuidStr.trim().isEmpty()) {
            handleMatchError(req, resp, MATCH_ID_REQUIRED_TITLE,
                    MATCH_ID_REQUIRED_DESCRIPTION);
            return;
        }

        UUID uuid;
        try {
            uuid = java.util.UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            handleMatchError(req, resp, INVALID_FORMAT_TITLE,
                    INVALID_FORMAT_DESCRIPTION);
            return;
        }

        MatchWithScore matchWithScore = ongoingMatchesService.find(uuid);
        if (matchWithScore == null) {
            handleMatchError(req, resp, MATCH_NOT_FOUND_TITLE,
                    MATCH_NOT_FOUND_DESCRIPTION);
            return;
        }

        req.setAttribute(MATCH_WITH_SCORE_ATTR, matchWithScore);
        req.setAttribute(CURRENT_SCORE_ATTR, matchWithScore.score());
        req.setAttribute(PLAYER_1_ATTR, matchWithScore.match().getFirstPlayer());
        req.setAttribute(PLAYER_2_ATTR, matchWithScore.match().getSecondPlayer());
        req.setAttribute(UUID_ATTR, uuid);
        getServletContext().getRequestDispatcher(MATCH_SCORE_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuidStr = req.getParameter("uuid");
        String playerWonPointId = req.getParameter(PLAYER_WON_POINT_ID_PARAM);
        UUID uuid = java.util.UUID.fromString(uuidStr);
        MatchWithScore matchWithScore = ongoingMatchesService.find(uuid);

        Player firstPlayer = matchWithScore.match().getFirstPlayer();
        Player secondPlayer = matchWithScore.match().getSecondPlayer();

        Player pointWinner = playerWonPointId.equals(firstPlayer.getId().toString()) ? firstPlayer : secondPlayer;

        Score calculatedScore = scoreCalculationService.calculate(matchWithScore, pointWinner);

        if (calculatedScore.isMatchFinished()) {
            ongoingMatchesService.delete(uuid);
            matchWithScore.match().setWinner(pointWinner);
            finishedMatchService.saveToDatabase(matchWithScore.match());

            req.setAttribute(POINT_WINNER_ATTR, pointWinner);
            req.setAttribute(PLAYER_1_ATTR, firstPlayer);
            req.setAttribute(PLAYER_2_ATTR, secondPlayer);
            req.setAttribute(PLAYER_1_SETS_ATTR, matchWithScore.score().getSets(firstPlayer));
            req.setAttribute(PLAYER_2_SETS_ATTR, matchWithScore.score().getSets(secondPlayer));

            getServletContext().getRequestDispatcher(MATCH_RESULT_JSP).forward(req, resp);
        } else {
            resp.sendRedirect(MATCH_SCORE_REDIRECT_LINK + uuid);
        }
    }

    private void handleMatchError(HttpServletRequest req, HttpServletResponse resp,
                                  String title, String description)
            throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        req.setAttribute(ERROR_TITLE_ATTR, title);
        req.setAttribute(ERROR_DESCRIPTION_ATTR, description);
        req.setAttribute(REQUESTED_URL_ATTR, req.getRequestURI() +
                                             (req.getQueryString() != null ? "?" + req.getQueryString() : ""));

        req.getRequestDispatcher(MATCH_ERROR_JSP).forward(req, resp);
    }
}
