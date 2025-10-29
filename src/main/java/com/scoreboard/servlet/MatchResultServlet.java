package com.scoreboard.servlet;

import com.scoreboard.constant.WebPaths;
import com.scoreboard.dto.response.MatchResult;
import com.scoreboard.exception.NotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/match-result")
public class MatchResultServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(MatchResultServlet.class);
    private static final String MATCH_RESULT_ATTRIBUTE = "matchResult";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        MatchResult matchResult = (MatchResult) session.getAttribute(MATCH_RESULT_ATTRIBUTE);

        if (matchResult == null) {
            logger.warn("Match result not found in session");
            throw new NotFoundException("Match result not found");
        }

        session.removeAttribute(MATCH_RESULT_ATTRIBUTE);

        req.setAttribute(MATCH_RESULT_ATTRIBUTE, matchResult);
        getServletContext().getRequestDispatcher(WebPaths.MATCH_RESULT_JSP).forward(req, resp);
    }
}