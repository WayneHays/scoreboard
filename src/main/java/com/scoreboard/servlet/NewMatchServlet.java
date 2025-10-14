package com.scoreboard.servlet;

import com.scoreboard.exception.ValidationException;
import com.scoreboard.model.entity.Player;
import com.scoreboard.service.OngoingMatchesService;
import com.scoreboard.util.WebPaths;
import com.scoreboard.validator.PlayerNameValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
            throws IOException, ServletException {
        String player1name = req.getParameter("player1name");
        String player2name = req.getParameter("player2name");

        logger.info("New match creation attempt - Player1: '{}', Player2: '{}'",
                player1name, player2name);

        try {
            String player1ValidName = PlayerNameValidator.validate(player1name);
            String player2ValidName = PlayerNameValidator.validate(player2name);

            if (player1ValidName.equalsIgnoreCase(player2ValidName)) {
                logger.warn("Duplicate player names rejected: '{}'", player1ValidName);
                throw new ValidationException("Players cannot have the same name");
            }

            Player firstPlayer = new Player(player1ValidName);
            Player secondPlayer = new Player(player2ValidName);

            UUID uuid = ongoingMatchesService.createMatch(firstPlayer, secondPlayer);
            logger.info("New match created successfully - UUID: {}, Players: {} vs {}",
                    uuid, player1ValidName, player2ValidName);

            resp.sendRedirect(req.getContextPath() + "/match-score?uuid=" + uuid);

        } catch (ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("firstPlayerInput", player1name);
            req.setAttribute("secondPlayerInput", player2name);
            getServletContext().getRequestDispatcher(WebPaths.NEW_MATCH_JSP).forward(req, resp);
        }
    }
}
