package com.scoreboard.util;

import com.scoreboard.model.Match;
import com.scoreboard.model.MatchWithScore;
import com.scoreboard.model.Player;
import com.scoreboard.servlet.UuidErrorType;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public final class RequestAttributeHelper {

    private RequestAttributeHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void setOngoingMatchAttributes(HttpServletRequest req, MatchWithScore match, UUID uuid) {
        req.setAttribute("matchWithScore", match);
        req.setAttribute("currentScore", match.score());
        req.setAttribute("player1", match.match().getFirstPlayer());
        req.setAttribute("player2", match.match().getSecondPlayer());
        req.setAttribute("uuid", uuid);
    }

    public static void setFinishedMatchAttributes(HttpServletRequest req, MatchWithScore match, Player winner) {
        Player firstPlayer = match.match().getFirstPlayer();
        Player secondPlayer = match.match().getSecondPlayer();

        req.setAttribute("winner", winner);
        req.setAttribute("player1", firstPlayer);
        req.setAttribute("player2", secondPlayer);
        req.setAttribute("player1sets", match.score().getSets(firstPlayer));
        req.setAttribute("player2sets", match.score().getSets(secondPlayer));
    }

    public static void setMatchesPageAttributes(HttpServletRequest req, int pageNumber,
                                                List<Match> matches, int totalPages,
                                                String filterByPlayerName, String errorMessage) {
        req.setAttribute("page", pageNumber);
        req.setAttribute("matches", matches);
        req.setAttribute("totalCountOfPages", totalPages);

        if (filterByPlayerName != null) {
            req.setAttribute("filter_by_player_name", filterByPlayerName);
        }
        if (errorMessage != null) {
            req.setAttribute("errorMessage", errorMessage);
        }
    }

    public static void set404ErrorAttributes(HttpServletRequest req,
                                             String errorMessage, String requestedUrl) {
        req.setAttribute("errorCode", 404);
        req.setAttribute("errorMessage", errorMessage);
        req.setAttribute("requestedUrl", requestedUrl);
    }

    public static void setMatchErrorAttributes(HttpServletRequest req, UuidErrorType errorType, String requestedUrl) {
        req.setAttribute("errorTitle", errorType.getTitle());
        req.setAttribute("errorDescription", errorType.getMessage());
        req.setAttribute("requestedUrl", requestedUrl);
    }
}
