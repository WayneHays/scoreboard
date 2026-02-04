package com.scoreboard.servlet;

import com.scoreboard.dto.OngoingMatchDto;
import com.scoreboard.exception.UuidParsingException;
import com.scoreboard.mapper.MatchResultMapper;
import com.scoreboard.service.MatchFacade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreServlet extends BaseServlet {
    private static final String MATCH_SCORE_JSP_NAME = "match-score";
    private static final String RESULTS_JSP_NAME = "match-result";

    private static final String UUID_PARAM = "uuid";
    private static final String PLAYER_NAME_PARAM = "playerName";

    private static final String UUID_REQUIRED_MSG = "UUID is required";
    private static final String INVALID_UUID_FORMAT = "Invalid UUID format";

    private static final String MATCH_RESULT = "matchResult";
    private static final String MATCH_DTO = "matchDto";

    private MatchFacade matchFacade;
    private MatchResultMapper resultMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.matchFacade = getService(MatchFacade.class);
        this.resultMapper = getService(MatchResultMapper.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID matchId = parse(req.getParameter(UUID_PARAM));
        OngoingMatchDto dto = matchFacade.getMatchDto(matchId);

        req.setAttribute(MATCH_DTO, dto);
        forwardTo(MATCH_SCORE_JSP_NAME, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        UUID matchId = parse(req.getParameter(UUID_PARAM));
        String playerName = req.getParameter(PLAYER_NAME_PARAM);

        OngoingMatchDto dto = matchFacade.awardPoint(matchId, playerName);

        if (dto.isFinished()) {
            req.setAttribute(MATCH_RESULT, resultMapper.toResult(dto, playerName));
            forwardTo(RESULTS_JSP_NAME, req, resp);
            return;
        }

        req.setAttribute(MATCH_DTO, dto);
        forwardTo(MATCH_SCORE_JSP_NAME, req, resp);
    }

    private UUID parse(String str) {
        if (StringUtils.isBlank(str)) {
            throw new UuidParsingException(UUID_REQUIRED_MSG);
        }

        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            throw new UuidParsingException(INVALID_UUID_FORMAT);
        }
    }
}
