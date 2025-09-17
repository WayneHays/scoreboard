package com.scoreboard.servlet;

import com.scoreboard.dto.OngoingMatch;
import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;
import com.scoreboard.service.FinishedMatchService;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.service.ScoreCalculationService;
import com.scoreboard.util.ErrorHandler;
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
        OngoingMatch ongoingMatch = ongoingMatchesService.find(uuid);

        if (ongoingMatch == null) {
            ErrorHandler.handleHttpError(
                    req,
                    resp,
                    SC_NOT_FOUND,
                    "Match not found");
            return;
        }
        req.setAttribute("ongoingMatch", ongoingMatch);
        getServletContext().getRequestDispatcher(MATCH_SCORE_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuidStr = req.getParameter("uuid");
        String playerWonPointId = req.getParameter("playerWonPointId");
        UUID uuid = parseUuid(uuidStr);

        if (playerWonPointId == null || playerWonPointId.isBlank()) {
            ErrorHandler.handleHttpError(
                    req,
                    resp,
                    SC_BAD_REQUEST,
                    "Player ID is required");
            return;
        }

        OngoingMatch ongoingMatch = ongoingMatchesService.find(uuid);

        if (ongoingMatch == null) {
            ErrorHandler.handleHttpError(
                    req,
                    resp,
                    SC_NOT_FOUND,
                    "Match not found");
            return;
        }

        processPointWin(req, resp, ongoingMatch, playerWonPointId);
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
            String playerWonPointId) throws ServletException, IOException {

        Player pointWinner = determinePointWinner(ongoingMatch, playerWonPointId);
        Match match = ongoingMatch.match();
        Score score = ongoingMatch.gameState().score();

        scoreCalculationService.calculate(match, score, pointWinner);

        if (scoreCalculationService.isMatchFinished(score, match.getFirstPlayer(), match.getSecondPlayer())) {
            finishMatch(req, resp, ongoingMatch, pointWinner);
        } else {
            continueMatch(resp, ongoingMatch);
        }
    }

    private Player determinePointWinner(OngoingMatch ongoingMatch, String playerWonPointId) {
        Player firstPlayer = ongoingMatch.match().getFirstPlayer();
        Player secondPlayer = ongoingMatch.match().getSecondPlayer();

        if (playerWonPointId.equals(firstPlayer.getId().toString())) {
            return firstPlayer;
        } else if (playerWonPointId.equals(secondPlayer.getId().toString())) {
            return secondPlayer;
        } else {
            throw new IllegalArgumentException("Player with ID " + playerWonPointId + " not found in this match");
        }
    }

    private void finishMatch(
            HttpServletRequest req,
            HttpServletResponse resp,
            OngoingMatch ongoingMatch,
            Player pointWinner)
            throws ServletException, IOException {

        ongoingMatchesService.delete(ongoingMatch.uuid());
        ongoingMatch.match().setWinner(pointWinner);
        finishedMatchService.saveToDatabase(ongoingMatch.match());
        req.setAttribute("ongoingMatch", ongoingMatch);
        getServletContext().getRequestDispatcher("/WEB-INF/match-result.jsp").forward(req, resp);
    }

    private void continueMatch(HttpServletResponse resp,
                               OngoingMatch ongoingMatch)
            throws IOException {
        resp.sendRedirect("/match-score?uuid=" + ongoingMatch.uuid());
    }
}
