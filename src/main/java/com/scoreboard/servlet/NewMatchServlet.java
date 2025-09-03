package com.scoreboard.servlet;

import com.scoreboard.model.Player;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.service.PlayerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/new-match")
public class NewMatchServlet extends HttpServlet {
    private PlayerService playerService = PlayerService.getInstance();
    private OngoingMatchesService ongoingMatchesService = OngoingMatchesService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/WEB-INF/new-match.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String firstPlayerName = req.getParameter("player1name");
        String secondPlayerName = req.getParameter("player2name");
        Player player1 = playerService.create(firstPlayerName);
        Player player2 = playerService.create(secondPlayerName);
        UUID uuid = ongoingMatchesService.create(player1, player2);

        resp.sendRedirect("/match-score?uuid=" + uuid);
    }
}
