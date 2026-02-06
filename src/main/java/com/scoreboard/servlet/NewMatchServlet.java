package com.scoreboard.servlet;

import com.scoreboard.service.OngoingMatchService;
import com.scoreboard.constant.ServletPath;
import com.scoreboard.validation.result.PairValidationResult;
import com.scoreboard.validation.PlayerNameValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@WebServlet("/new-match")
@Slf4j
public class NewMatchServlet extends BaseServlet {
    private static final String JSP_NAME = "new-match";

    private static final String VALIDATION_RESULT = "validationResult";
    private static final String UUID_PARAM = "uuid";
    private static final String FIRST_PLAYER_NAME = "firstPlayerName";
    private static final String SECOND_PLAYER_NAME = "secondPlayerName";;
    public static final String VALIDATION_FAIL_MESSAGE = "Player names validation failed: {}";

    private OngoingMatchService ongoingMatchService;
    private PlayerNameValidator validator;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ongoingMatchService = getService(OngoingMatchService.class);
        this.validator = getService(PlayerNameValidator.class);
        log.debug("NewMatchServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forwardTo(JSP_NAME, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String firstPlayerName = req.getParameter(FIRST_PLAYER_NAME).trim();
        String secondPlayerName = req.getParameter(SECOND_PLAYER_NAME).trim();
        PairValidationResult result = validator.validatePair(firstPlayerName, secondPlayerName);

        if (result.hasErrors()) {
            log.info(VALIDATION_FAIL_MESSAGE, result);
            req.setAttribute(VALIDATION_RESULT, result);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            forwardTo(JSP_NAME, req, resp);
            return;
        }

        UUID matchId = ongoingMatchService.createMatch(firstPlayerName, secondPlayerName);
        redirectTo(ServletPath.MATCH_SCORE, Map.of(UUID_PARAM, matchId), req, resp);
    }
}
