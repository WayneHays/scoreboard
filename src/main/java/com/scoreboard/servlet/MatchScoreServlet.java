package com.scoreboard.servlet;

import com.scoreboard.model.MatchWithScore;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;
import com.scoreboard.service.FinishedMatchService;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.service.PlayerService;
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
    private OngoingMatchesService ongoingMatchesService = OngoingMatchesService.getInstance();
    private ScoreCalculationService scoreCalculationService = ScoreCalculationService.getInstance();
    private FinishedMatchService finishedMatchService = FinishedMatchService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuidStr = req.getParameter("uuid");
        UUID uuid = UUID.fromString(uuidStr);
        MatchWithScore matchWithScore = ongoingMatchesService.find(uuid);

        req.setAttribute("matchWithScore", matchWithScore);
        req.setAttribute("currentScore", matchWithScore.score());
        req.setAttribute("player1", matchWithScore.match().getFirstPlayer());
        req.setAttribute("player2", matchWithScore.match().getSecondPlayer());
        req.setAttribute("uuid", uuid);
        getServletContext().getRequestDispatcher("/WEB-INF/match-score.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuidStr = req.getParameter("uuid");
        String playerWonPointId = req.getParameter("playerWonPointId");
        UUID uuid = UUID.fromString(uuidStr);
        MatchWithScore matchWithScore = ongoingMatchesService.find(uuid);

        Player firstPlayer = matchWithScore.match().getFirstPlayer();
        Player secondPlayer = matchWithScore.match().getSecondPlayer();

        Player pointWinner = playerWonPointId.equals(firstPlayer.getId().toString()) ? firstPlayer : secondPlayer;

        Score calculatedScore = scoreCalculationService.calculate(matchWithScore, pointWinner);

        if (calculatedScore.isMatchFinished()) {
            ongoingMatchesService.delete(uuid);
            finishedMatchService.saveToDatabase(matchWithScore.match());
            getServletContext().getRequestDispatcher("/WEB-INF/match-result.jsp").forward(req, resp);
        }
        resp.sendRedirect("/match-score?uuid=" + uuid);
    }
}
