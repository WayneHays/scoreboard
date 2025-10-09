package com.scoreboard.servlet;

import com.scoreboard.config.ApplicationContext;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.service.PlayerService;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.model.entity.Player;
import com.scoreboard.util.WebPaths;
import com.scoreboard.validator.PlayerNameValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/new-match")
public class NewMatchServlet extends HttpServlet {
    private final OngoingMatchesService ongoingMatchesService;
    private final PlayerService playerService;

    public NewMatchServlet() {
        this.ongoingMatchesService = ApplicationContext.get(OngoingMatchesService.class);
        this.playerService = ApplicationContext.get(PlayerService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().getRequestDispatcher(WebPaths.NEW_MATCH_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String firstPlayerInput = req.getParameter("firstPlayerInput");
        String secondPlayerInput = req.getParameter("secondPlayerInput");

        try {
            String firstPlayerName = PlayerNameValidator.validate(firstPlayerInput);
            String secondPlayerName = PlayerNameValidator.validate(secondPlayerInput);

            if (firstPlayerName.equalsIgnoreCase(secondPlayerName)) {
                throw new ValidationException("Players cannot have the same name");
            }

            Player firstPlayer = findOrCreatePlayer(firstPlayerName);
            Player secondPlayer = findOrCreatePlayer(secondPlayerName);
            UUID uuid = ongoingMatchesService.createMatch(firstPlayer, secondPlayer);
            resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + uuid);

        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("firstPlayerInput", firstPlayerInput);
            req.setAttribute("secondPlayerInput", secondPlayerInput);
            getServletContext().getRequestDispatcher(WebPaths.NEW_MATCH_JSP).forward(req, resp);
        }
    }

    private Player findOrCreatePlayer(String name) {
        return playerService.find(name)
                .orElseGet(() -> {
                    try {
                        return playerService.create(name);
                    } catch (Exception e) {
                        return playerService.find(name)
                                .orElseThrow(() -> new RuntimeException("Failed to find or create player", e));
                    }
                });
    }
}
