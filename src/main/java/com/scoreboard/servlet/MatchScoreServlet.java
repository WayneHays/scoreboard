package com.scoreboard.servlet;

import com.scoreboard.dto.MatchResultDto;
import com.scoreboard.dto.OngoingMatchDto;
import com.scoreboard.service.MatchFacade;
import com.scoreboard.constant.ServletPath;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreServlet extends BaseServlet {
    private static final String MATCH_SCORE_JSP = "match-score";
    private static final String MATCH_RESULT_JSP = "match-result";

    private static final String MATCH_DTO = "matchDto";
    private static final String MATCH_RESULT_DTO = "result";
    private static final String PLAYER_NAME_PARAM = "playerName";

    private MatchFacade matchFacade;

    @Override
    public void init() throws ServletException {
        super.init();
        this.matchFacade = getService(MatchFacade.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID matchId = parse(req.getParameter(UUID_PARAM));
        OngoingMatchDto dto = matchFacade.getOngoingMatch(matchId);

        req.setAttribute(MATCH_DTO, dto);
        forwardTo(MATCH_SCORE_JSP, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        UUID matchId = parse(req.getParameter(UUID_PARAM));
        String playerName = req.getParameter(PLAYER_NAME_PARAM);

        OngoingMatchDto dto = matchFacade.awardPoint(matchId, playerName);

        if (dto.isFinished()) {
            MatchResultDto result = matchFacade.getMatchResultAndRemove(matchId);
            req.setAttribute(MATCH_RESULT_DTO, result);
            forwardTo(MATCH_RESULT_JSP, req, resp);
            return;
        }

        redirectTo(ServletPath.MATCH_SCORE, Map.of(UUID_PARAM, matchId), req, resp);
    }
}
