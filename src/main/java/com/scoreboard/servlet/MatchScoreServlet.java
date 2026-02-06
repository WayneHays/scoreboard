package com.scoreboard.servlet;

import com.scoreboard.dto.MatchResponse.MatchResponse;
import com.scoreboard.dto.MatchResponse.OngoingMatchDto;
import com.scoreboard.exception.UuidParsingException;
import com.scoreboard.service.MatchFacade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreServlet extends BaseServlet {
    private static final String JSP_SCORE = "match-score";
    private static final String JSP_RESULT = "match-result";
    private static final String ATTR_MATCH = "match";
    private static final String ATTR_RESULT = "result";
    private static final String PARAM_PLAYER_NAME = "playerName";
    private static final String PARAM_UUID = "uuid";
    private static final String MSG_UUID_REQUIRED = "UUID is required";
    private static final String MSG_INVALID_UUID_FORMAT = "Invalid UUID format";

    private MatchFacade matchFacade;

    @Override
    public void init() throws ServletException {
        super.init();
        this.matchFacade = getService(MatchFacade.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID matchId = parse(req.getParameter(PARAM_UUID));
        OngoingMatchDto match = matchFacade.getOngoingMatch(matchId);

        req.setAttribute(ATTR_MATCH, match);
        forwardTo(JSP_SCORE, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        UUID matchId = parse(req.getParameter(PARAM_UUID));
        String playerName = req.getParameter(PARAM_PLAYER_NAME);

        MatchResponse matchResponse = matchFacade.processPoint(matchId, playerName);

        if (matchResponse.isFinished()) {
            req.setAttribute(ATTR_RESULT, matchResponse.matchResult());
            forwardTo(JSP_RESULT, req, resp);
            return;
        }

        redirectTo(JSP_SCORE, Map.of(PARAM_UUID, matchId), req, resp);
    }


    private UUID parse(String str) {
        if (StringUtils.isBlank(str)) {
            throw new UuidParsingException(MSG_UUID_REQUIRED);
        }

        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            throw new UuidParsingException(MSG_INVALID_UUID_FORMAT);
        }
    }
}
