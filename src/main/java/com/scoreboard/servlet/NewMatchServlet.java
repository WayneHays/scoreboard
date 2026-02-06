package com.scoreboard.servlet;

import com.scoreboard.service.OngoingMatchService;
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
    private static final String JSP_NEW_MATCH = "new-match";
    private static final String JSP_SCORE = "match-score";
    private static final String PARAM_UUID = "uuid";
    private static final String PARAM_FIRST_PLAYER_NAME = "firstPlayerName";
    private static final String PARAM_SECOND_PLAYER_NAME = "secondPlayerName";
    private static final String ATTR_VALIDATION_RESULT = "validationResult";
    private static final String MSG_VALIDATION_FAIL = "Player names validation failed: {}";
    private static final String MSG_INIT_SUCCESS = "NewMatchServlet initialized";

    private OngoingMatchService ongoingMatchService;
    private PlayerNameValidator validator;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ongoingMatchService = getService(OngoingMatchService.class);
        this.validator = getService(PlayerNameValidator.class);
        log.debug(MSG_INIT_SUCCESS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forwardTo(JSP_NEW_MATCH, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String firstPlayerName = req.getParameter(PARAM_FIRST_PLAYER_NAME).trim();
        String secondPlayerName = req.getParameter(PARAM_SECOND_PLAYER_NAME).trim();
        PairValidationResult result = validator.validatePair(firstPlayerName, secondPlayerName);

        if (result.hasErrors()) {
            log.info(MSG_VALIDATION_FAIL, result);
            req.setAttribute(ATTR_VALIDATION_RESULT, result);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            forwardTo(JSP_NEW_MATCH, req, resp);
            return;
        }

        UUID matchId = ongoingMatchService.createMatch(firstPlayerName, secondPlayerName);
        redirectTo(JSP_SCORE, Map.of(PARAM_UUID, matchId), req, resp);
    }
}
