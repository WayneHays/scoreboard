package com.scoreboard.servlet;

import com.scoreboard.exception.PairNameValidationException;
import com.scoreboard.service.OngoingMatchesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@WebServlet("/new-match")
@Slf4j
public class NewMatchServlet extends BaseServlet {
    private static final String MATCH_CREATION_JSP_NAME = "new-match";
    private static final String MATCH_SCORE_JSP_NAME = "match-score";

    private static final String COMMON_ERRORS = "commonErrors";
    private static final String FIRST_NAME_ERRORS = "firstNameErrors";
    private static final String SECOND_NAME_ERRORS = "secondNameErrors";

    private static final String FIRST_PLAYER_NAME = "firstPlayerName";
    private final String SECOND_PLAYER_NAME = "secondPlayerName";

    private OngoingMatchesService ongoingMatchesService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ongoingMatchesService = getService(OngoingMatchesService.class);
        log.debug("NewMatchServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forwardTo(MATCH_CREATION_JSP_NAME, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String firstPlayerName = req.getParameter(FIRST_PLAYER_NAME).trim();
        String secondPlayerName = req.getParameter(SECOND_PLAYER_NAME).trim();

        try {
            UUID matchId = ongoingMatchesService.createMatch(firstPlayerName, secondPlayerName);
            redirectTo(MATCH_SCORE_JSP_NAME, Map.of("uuid", matchId), req, resp);

        } catch (PairNameValidationException e) {
            setAttributeIfNotEmpty(req, COMMON_ERRORS, e.getCommonErrors());
            setAttributeIfNotEmpty(req, FIRST_NAME_ERRORS, e.getFirstNameErrors());
            setAttributeIfNotEmpty(req, SECOND_NAME_ERRORS, e.getSecondNameErrors());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            forwardTo(MATCH_CREATION_JSP_NAME, req, resp);
        }
    }

    private void setAttributeIfNotEmpty(
            HttpServletRequest req,
            String attributeName,
            Collection<?> collection
    ) {
        if (ObjectUtils.isNotEmpty(collection)) {
            req.setAttribute(attributeName, collection);
        }
    }
}
