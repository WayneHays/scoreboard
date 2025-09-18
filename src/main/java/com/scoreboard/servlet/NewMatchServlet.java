package com.scoreboard.servlet;

import com.scoreboard.dto.NewMatchForm;
import com.scoreboard.model.Player;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.service.PlayerService;
import com.scoreboard.validator.PlayerNameValidator;
import com.scoreboard.validator.ValidationResult;
import com.scoreboard.util.WebPaths;
import com.scoreboard.mapper.NewMatchFormMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@WebServlet(WebPaths.NEW_MATCH_URL)
public class NewMatchServlet extends HttpServlet {
    private final PlayerNameValidator playerNameValidator = new PlayerNameValidator();
    private final NewMatchFormMapper formMapper = new NewMatchFormMapper();
    private final OngoingMatchesService ongoingMatchesService = OngoingMatchesService.getInstance();
    private final PlayerService playerService = PlayerService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().getRequestDispatcher(WebPaths.NEW_MATCH_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String player1Input = req.getParameter("player1name");
        String player2Input = req.getParameter("player2name");

        ValidationResult player1Result = playerNameValidator.validate(player1Input);
        ValidationResult player2Result = playerNameValidator.validate(player2Input);

        if (hasValidationErrors(player1Result, player2Result)) {
            showFormWithErrors(req, resp, player1Result, player2Result, player1Input, player2Input,null);
            return;
        }

        if (hasDuplicateNames(player1Result, player2Result)) {
            showFormWithErrors(req, resp, player1Result, player2Result,player1Input, player2Input,
                    "Players cannot have the same name");
            return;
        }

        createMatchAndRedirect(req, resp, player1Result, player2Result);
    }

    private void createMatchAndRedirect(HttpServletRequest req,HttpServletResponse resp, ValidationResult player1Result,
                                        ValidationResult player2Result) throws IOException {
        Player player1 = findOrCreatePlayer(player1Result.value());
        Player player2 = findOrCreatePlayer(player2Result.value());
        UUID uuid = ongoingMatchesService.createMatch(player1, player2);
        resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + uuid);
    }

    private void showFormWithErrors(HttpServletRequest req, HttpServletResponse resp,
                                    ValidationResult player1Result, ValidationResult player2Result,
                                    String player1Input, String player2Input, String generalError)
            throws ServletException, IOException {
        NewMatchForm form = formMapper.map(
                player1Input,
                player2Input,
                player1Result,
                player2Result,
                generalError
        );

        req.setAttribute("newMatchForm", form);
        getServletContext().getRequestDispatcher(WebPaths.NEW_MATCH_JSP).forward(req, resp);
    }

    private boolean hasDuplicateNames(ValidationResult player1Result, ValidationResult player2Result) {
        return player1Result.value() != null && player2Result.value() != null &&
               player1Result.value().equalsIgnoreCase(player2Result.value());
    }

    private boolean hasValidationErrors(ValidationResult player1Result, ValidationResult player2Result) {
        return !player1Result.isValid() || !player2Result.isValid();
    }

    private Player findOrCreatePlayer(String name) {
        return playerService.find(name)
                .orElseGet(() -> playerService.create(name));
    }
}
