package com.scoreboard.servlet;

import com.scoreboard.constant.WebPaths;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.model.entity.Player;
import com.scoreboard.service.ongoingmatchesservice.OngoingMatchesService;
import com.scoreboard.validator.PlayerNameValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/new-match")
public class NewMatchServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(NewMatchServlet.class);
    private OngoingMatchesService ongoingMatchesService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ongoingMatchesService = getService(OngoingMatchesService.class);
        logger.debug("NewMatchServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().getRequestDispatcher(WebPaths.NEW_MATCH_JSP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException{
        String player1name = req.getParameter("player1name");
        String player2name = req.getParameter("player2name");

        logger.debug("Match creation attempt: '{}' vs '{}'", player1name, player2name);

        try {
            UUID uuid = createMatch(player1name, player2name);
            logger.info("Match created: UUID={}, players='{}' vs '{}'", uuid, player1name, player2name);

            resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + uuid);

        } catch (ValidationException e) {
            logger.debug("Validation failed: {}", e.getMessage());

            HttpSession session = req.getSession();
            session.setAttribute("error", e.getMessage());
            session.setAttribute("player1Input", player1name);
            session.setAttribute("player2Input", player2name);

            resp.sendRedirect(req.getContextPath() + "/new-match");
        }
    }

    private UUID createMatch(String player1name, String player2name) {
        String player1ValidName = PlayerNameValidator.validate(player1name);
        String player2ValidName = PlayerNameValidator.validate(player2name);

        if (player1ValidName.equalsIgnoreCase(player2ValidName)) {
            throw new ValidationException("Players cannot have the same name");
        }

        Player firstPlayer = new Player(player1ValidName);
        Player secondPlayer = new Player(player2ValidName);

        return ongoingMatchesService.createMatch(firstPlayer, secondPlayer);
    }
}
