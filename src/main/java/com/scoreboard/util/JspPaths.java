package com.scoreboard.util;

public final class JspPaths {
    public static final String ERROR_JSP = "/WEB-INF/error.jsp";
    public static final String HOME_JSP = "/WEB-INF/home.jsp";
    public static final String MATCHES_JSP = "/WEB-INF/matches.jsp";
    public static final String MATCH_SCORE_JSP = "/WEB-INF/match-score.jsp";
    public static final String MATCH_RESULT_JSP = "/WEB-INF/match-result.jsp";
    public static final String NEW_MATCH_JSP = "/WEB-INF/new-match.jsp";

    private JspPaths() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

}
