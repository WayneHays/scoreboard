package com.scoreboard.servlet;

import com.scoreboard.dto.NewMatchForm;
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
import java.util.regex.Pattern;

@WebServlet("/new-match")
public class NewMatchServlet extends HttpServlet {
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 30;
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яёА-ЯЁ\\s-']+$");
    private static final String NEW_MATCH_JSP = "/WEB-INF/new-match.jsp";

    private final PlayerService playerService = PlayerService.getInstance();
    private final OngoingMatchesService ongoingMatchesService = OngoingMatchesService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().getRequestDispatcher(NEW_MATCH_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException{
        String player1Input = req.getParameter("player1name");
        String player2Input = req.getParameter("player2name");

        ValidationResult player1Result = validate(player1Input);
        ValidationResult player2Result = validate(player2Input);

        if (hasValidationErrors(player1Result, player2Result)) {
            showFormWithErrors(
                    req,
                    resp,
                    player1Result,
                    player2Result,
                    player1Input,
                    player2Input,
                    null);
            return;
        }

        if (hasDuplicateNames(player1Result, player2Result)) {
            showFormWithErrors(
                    req,
                    resp,
                    player1Result,
                    player2Result,
                    player1Input,
                    player2Input,
                    "Players cannot have the same name");
            return;
        }

        createMatchAndRedirect(resp, player1Result, player2Result);
    }

    private void createMatchAndRedirect(HttpServletResponse resp, ValidationResult player1Result,
                                        ValidationResult player2Result) throws IOException {
        Player player1 = playerService.findByNameOrCreate(player1Result.value());
        Player player2 = playerService.findByNameOrCreate(player2Result.value());
        UUID uuid = ongoingMatchesService.createMatch(player1, player2);
        resp.sendRedirect("/match-score?uuid=" + uuid);
    }

    private void showFormWithErrors(HttpServletRequest req, HttpServletResponse resp,
                                    ValidationResult player1Result, ValidationResult player2Result,
                                    String player1Input, String player2Input, String generalError)
            throws ServletException, IOException {
        NewMatchForm form = new NewMatchForm(
                player1Input != null ? player1Input : "",
                player2Input != null ? player2Input : "",
                player1Result.errorMessage(),
                player2Result.errorMessage(),
                generalError
        );

        req.setAttribute("newMatchForm", form);
        getServletContext().getRequestDispatcher(NEW_MATCH_JSP).forward(req, resp);
    }

    private boolean hasDuplicateNames(ValidationResult player1Result, ValidationResult player2Result) {
        return player1Result.value() != null && player2Result.value() != null &&
               player1Result.value().equalsIgnoreCase(player2Result.value());
    }

    private boolean hasValidationErrors(ValidationResult player1Result, ValidationResult player2Result) {
        return !player1Result.isValid() || !player2Result.isValid();
    }

    private ValidationResult validate(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ValidationResult.error("Player name is required");
        }
        String trimmed = name.trim();

        if (trimmed.length() < MIN_NAME_LENGTH) {
            return ValidationResult.error("Name too short (minimum %d characters)".formatted(MIN_NAME_LENGTH));
        }

        if (trimmed.length() > MAX_NAME_LENGTH) {
            return ValidationResult.error("Name too long (maximum %d characters)".formatted(MAX_NAME_LENGTH));
        }

        if (!VALID_NAME_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult.error("Name contains invalid characters (a-zA-Zа-яёА-ЯЁ symbols only)");
        }

        return ValidationResult.success(trimmed);
    }
}
