package com.scoreboard.servlet;

import com.scoreboard.model.MatchWithScore;
import com.scoreboard.model.Score;
import com.scoreboard.service.OngoingMatchesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreServlet extends HttpServlet {
    private OngoingMatchesService ongoingMatchesService = new OngoingMatchesService();
    private ScoreCalculationService scoreCalculationService = new ScoreCalculationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuidStr = req.getParameter("uuid");
        UUID uuid = UUID.fromString(uuidStr);
        MatchWithScore matchWithScore = ongoingMatchesService.find(uuid);
        Score score = matchWithScore.getScore();


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuidStr = req.getParameter("uuid");
        String playerWonPointId = req.getParameter("playerWonPointId");
        UUID uuid = UUID.fromString(uuidStr);
        MatchWithScore matchWithScore = ongoingMatchesService.find(uuid);
        scoreCalculationService.calculatePoints(matchWithScore, playerWonPointId);
    }
}
