package com.scoreboard.servlet;

import com.scoreboard.constant.JspPaths;
import com.scoreboard.exception.ValidationException;
import com.scoreboard.model.entity.Player;
import com.scoreboard.service.ongoingmatches.OngoingMatchesService;
import com.scoreboard.util.ServletHelper;
import com.scoreboard.validation.PlayerNameValidator;
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

    private static final String ERROR_ATTR = "error";
    private static final String PLAYER_1_INPUT_ATTR = "player1Input";
    private static final String PLAYER_2_INPUT_ATTR = "player2Input";
    private static final String DUPLICATE_PLAYERS_NAME_MESSAGE = "Players cannot have the same name";

    private OngoingMatchesService ongoingMatchesService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ongoingMatchesService = getService(OngoingMatchesService.class);
        logger.debug("NewMatchServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session != null) {
            String error = (String) session.getAttribute(ERROR_ATTR);
            if (error != null) {
                req.setAttribute(ERROR_ATTR, error);
                session.removeAttribute(ERROR_ATTR);
            }

            String player1 = (String) session.getAttribute(PLAYER_1_INPUT_ATTR);
            if (player1 != null) {
                req.setAttribute(PLAYER_1_INPUT_ATTR, player1);
                session.removeAttribute(PLAYER_1_INPUT_ATTR);
            }

            String player2 = (String) session.getAttribute(PLAYER_2_INPUT_ATTR);
            if (player2 != null) {
                req.setAttribute(PLAYER_2_INPUT_ATTR, player2);
                session.removeAttribute(PLAYER_2_INPUT_ATTR);
            }
        }

        ServletHelper.forwardToJsp(req, resp, JspPaths.NEW_MATCH_JSP);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String player1name = req.getParameter("player1name");
        String player2name = req.getParameter("player2name");

        logger.debug("Match creation attempt: '{}' vs '{}'", player1name, player2name);

        try {
            UUID uuid = createMatch(player1name, player2name);
            logger.info("Match created: UUID={}, players='{}' vs '{}'", uuid, player1name, player2name);
            ServletHelper.redirect(req, resp, "/match-score?uuid=" + uuid);

        } catch (ValidationException e) {
            logger.debug("Validation failed: {}", e.getMessage());

            HttpSession session = req.getSession();
            session.setAttribute(ERROR_ATTR, e.getMessage());
            session.setAttribute(PLAYER_1_INPUT_ATTR, player1name);
            session.setAttribute(PLAYER_2_INPUT_ATTR, player2name);

            ServletHelper.redirect(req, resp, "/new-match");
        }
    }

    private UUID createMatch(String player1name, String player2name) {
        String player1ValidName = PlayerNameValidator.validate(player1name);
        String player2ValidName = PlayerNameValidator.validate(player2name);

        if (player1ValidName.equalsIgnoreCase(player2ValidName)) {
            throw new ValidationException(DUPLICATE_PLAYERS_NAME_MESSAGE);
        }

        Player firstPlayer = new Player(player1ValidName);
        Player secondPlayer = new Player(player2ValidName);

        return ongoingMatchesService.createMatch(firstPlayer, secondPlayer);
    }
}
