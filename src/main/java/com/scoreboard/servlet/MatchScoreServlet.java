package com.scoreboard.servlet;

import com.scoreboard.dto.GameState;
import com.scoreboard.model.MatchWithScore;
import com.scoreboard.model.Player;
import com.scoreboard.service.FinishedMatchService;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.service.ScoreCalculationService;
import com.scoreboard.util.ErrorHandler;
import com.scoreboard.util.RequestAttributeHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@WebServlet("/match-score")
public class MatchScoreServlet extends HttpServlet {
    private static final String MATCH_SCORE_JSP = "/WEB-INF/match-score.jsp";
    private static final int REQUIRED_UUID_LENGTH = 36;

    private final OngoingMatchesService ongoingMatchesService = OngoingMatchesService.getInstance();
    private final ScoreCalculationService scoreCalculationService = ScoreCalculationService.getInstance();
    private final FinishedMatchService finishedMatchService = FinishedMatchService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuidStr = req.getParameter("uuid");
        UUID uuid = parseUuid(uuidStr);
        MatchWithScore matchWithScore = ongoingMatchesService.find(uuid);

        if (matchWithScore == null) {
            ErrorHandler.handleHttpError(req, resp, SC_NOT_FOUND, "Match not found");
            return;
        }
        GameState gameState = scoreCalculationService.getCurrentGameState(matchWithScore);
        RequestAttributeHelper.setOngoingMatchAttributes(req, matchWithScore, uuid, gameState);
        getServletContext().getRequestDispatcher(MATCH_SCORE_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuidStr = req.getParameter("uuid");
        String playerWonPointId = req.getParameter("playerWonPointId");
        UUID uuid = parseUuid(uuidStr);

        if (playerWonPointId == null || playerWonPointId.isBlank()) {
            ErrorHandler.handleHttpError(req, resp, SC_BAD_REQUEST, "Player ID is required");
            return;
        }
        MatchWithScore matchWithScore = ongoingMatchesService.find(uuid);

        if (matchWithScore == null) {
            ErrorHandler.handleHttpError(req, resp, SC_NOT_FOUND, "Match not found");
            return;
        }
        processPointWin(req, resp, matchWithScore, playerWonPointId, uuid);
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

    private void processPointWin(HttpServletRequest req, HttpServletResponse resp,
                                 MatchWithScore matchWithScore, String playerWonPointId, UUID uuid)
            throws ServletException, IOException {
        Player pointWinner = determinePointWinner(matchWithScore, playerWonPointId);
        GameState gameState = scoreCalculationService.calculate(matchWithScore, pointWinner);

        if (isMatchFinished(gameState)) {
            finishMatch(req, resp, matchWithScore, uuid, pointWinner);
        } else {
            continueMatch(req, resp, matchWithScore, uuid, gameState);
        }
    }

    private Player determinePointWinner(MatchWithScore matchWithScore, String playerWonPointId) {
        Player firstPlayer = matchWithScore.match().getFirstPlayer();
        Player secondPlayer = matchWithScore.match().getSecondPlayer();

        if (playerWonPointId.equals(firstPlayer.getId().toString())) {
            return firstPlayer;
        } else if (playerWonPointId.equals(secondPlayer.getId().toString())) {
            return secondPlayer;
        } else {
            throw new IllegalArgumentException("Player with ID " + playerWonPointId + " not found in this match");
        }
    }

    private boolean isMatchFinished(GameState gameState) {
        Player firstPlayer = null;
        Player secondPlayer = null;

        for (Player player : gameState.score().getPlayersPoints().keySet()) {
            if (firstPlayer == null) {
                firstPlayer = player;
            } else {
                secondPlayer = player;
            }
        }

        return scoreCalculationService.isMatchFinished(gameState.score(), firstPlayer, secondPlayer);
    }

    private void finishMatch(HttpServletRequest req, HttpServletResponse resp,
                             MatchWithScore matchWithScore, UUID uuid, Player pointWinner)
            throws ServletException, IOException {
        ongoingMatchesService.delete(uuid);
        matchWithScore.match().setWinner(pointWinner);
        finishedMatchService.saveToDatabase(matchWithScore.match());
        RequestAttributeHelper.setFinishedMatchAttributes(req, matchWithScore, pointWinner);
        getServletContext().getRequestDispatcher("/WEB-INF/match-result.jsp").forward(req, resp);
    }

    private void continueMatch(HttpServletRequest req, HttpServletResponse resp,
                               MatchWithScore matchWithScore, UUID uuid, GameState gameState)
            throws ServletException, IOException {
        RequestAttributeHelper.setOngoingMatchAttributes(req, matchWithScore, uuid, gameState);
        getServletContext().getRequestDispatcher(MATCH_SCORE_JSP).forward(req, resp);
    }
}
