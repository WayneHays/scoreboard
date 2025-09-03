package com.scoreboard.servlet;

import com.scoreboard.model.Player;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.service.PlayerService;
import com.scoreboard.util.PlayerNameValidator;
import com.scoreboard.util.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/new-match")
public class NewMatchServlet extends HttpServlet {
    private static final String NEW_MATCH = "/WEB-INF/new-match.jsp";
    private static final String MATCH_SCORE_UUID = "/match-score?uuid=";
    private static final String PLAYER_1_NAME_PARAM = "player1name";
    private static final String PLAYER_2_NAME_PARAM = "player2name";

    private PlayerService playerService = PlayerService.getInstance();
    private OngoingMatchesService ongoingMatchesService = OngoingMatchesService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().getRequestDispatcher(NEW_MATCH).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String player1Input = req.getParameter(PLAYER_1_NAME_PARAM);
        String player2Input = req.getParameter(PLAYER_2_NAME_PARAM);

        ValidationResult player1Result = PlayerNameValidator.validate(player1Input);
        ValidationResult player2Result = PlayerNameValidator.validate(player2Input);

        if (hasValidationErrors(player1Result, player2Result)) {
            handleValidationErrors(req, resp, player1Result, player2Result, player1Input, player2Input);
            return;
        }

        if (hasDuplicatedNames(player1Result, player2Result)) {
            handleDuplicateNames(req, resp, player1Input, player2Input);
            return;
        }

        Player player1 = playerService.create(player1Result.value());
        Player player2 = playerService.create(player2Result.value());
        UUID uuid = ongoingMatchesService.create(player1, player2);
        resp.sendRedirect(MATCH_SCORE_UUID + uuid);
    }

    private void handleDuplicateNames(
            HttpServletRequest req,
            HttpServletResponse resp,
            String player1Input,
            String player2Input)
            throws ServletException, IOException {
        req.setAttribute("generalError", "Players cannot have the same name");
        req.setAttribute("player1Value", player1Input);
        req.setAttribute("player2Value", player2Input);
        getServletContext().getRequestDispatcher(NEW_MATCH).forward(req, resp);
    }

    private void handleValidationErrors(
            HttpServletRequest req,
            HttpServletResponse resp,
            ValidationResult player1Result,
            ValidationResult player2Result,
            String player1Input,
            String player2Input)
            throws ServletException, IOException {
        if (player1Result.errorMessage() != null) {
            req.setAttribute("player1Error", player1Result.errorMessage());
        }
        if (player2Result.errorMessage() != null) {
            req.setAttribute("player2Error", player2Result.errorMessage());
        }
        req.setAttribute("player1Value", player1Input);
        req.setAttribute("player2Value", player2Input);
        getServletContext().getRequestDispatcher(NEW_MATCH).forward(req, resp);
    }

    private boolean hasDuplicatedNames(ValidationResult player1Result, ValidationResult player2Result) {
        return player1Result.value() != null && player2Result.value() != null &&
               player1Result.value().equalsIgnoreCase(player2Result.value());
    }

    private boolean hasValidationErrors(ValidationResult player1Result, ValidationResult player2Result) {
        return !player1Result.isValid() || !player2Result.isValid();
    }
}
