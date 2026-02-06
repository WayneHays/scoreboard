package com.scoreboard.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServletPath {
    public static final String MATCH_SCORE = "/match-score";
    public static final String MATCH_RESULT = "/match-result";
    public static final String MATCHES = "/matches";
    public static final String HOME = "/home";
    public static final String NEW_MATCH = "/new-match";
    public static final String ERROR = "/error";
}
