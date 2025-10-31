package com.scoreboard.servlet;

import com.scoreboard.model.domain.OngoingMatch;
import com.scoreboard.service.matchprocess.MatchProcessor;
import com.scoreboard.service.matchprocess.MatchViewResolver;
import com.scoreboard.util.ServletHelper;
import com.scoreboard.validation.PlayerNameValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(MatchScoreServlet.class);
    private static final String UUID_PARAM = "uuid";
    private static final String PLAYER_NAME_PARAM = "playerName";

    private MatchProcessor matchProcessor;
    private MatchViewResolver viewResolver;

    @Override
    public void init() throws ServletException {
        super.init();
        this.matchProcessor = getService(MatchProcessor.class);
        this.viewResolver = getService(MatchViewResolver.class);
        logger.debug("MatchScoreServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID matchId = ServletHelper.parseUuid(req.getParameter(UUID_PARAM), UUID_PARAM);
        logger.debug("Displaying match score: {}", matchId);

        OngoingMatch match = matchProcessor.getMatch(matchId);
        Object view = viewResolver.resolveView(match);
        String jspPath = viewResolver.resolveJspPath(match);
        String attributeName = viewResolver.resolveAttributeName(match);

        req.setAttribute(attributeName, view);
        ServletHelper.forwardToJsp(req, resp, jspPath);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UUID matchId = ServletHelper.parseUuid(req.getParameter(UUID_PARAM), UUID_PARAM);
        String playerName = PlayerNameValidator.validate(req.getParameter(PLAYER_NAME_PARAM));
        logger.debug("Processing point for match: {}, player: {}", matchId, playerName);

        matchProcessor.processPoint(matchId, playerName);
        logger.debug("Point processed, redirecting to match: {}", matchId);
        ServletHelper.redirect(req, resp, "/match-score?uuid=" + matchId);
    }
}
