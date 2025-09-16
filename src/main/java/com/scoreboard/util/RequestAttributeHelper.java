package com.scoreboard.util;

import com.scoreboard.dto.GameState;
import com.scoreboard.model.Match;
import com.scoreboard.model.MatchWithScore;
import com.scoreboard.model.Player;
import com.scoreboard.servlet.ValidationResult;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public final class RequestAttributeHelper {

    private RequestAttributeHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void setOngoingMatchAttributes(HttpServletRequest req, MatchWithScore matchWithScore,
                                                 UUID uuid, GameState gameState) {

        req.setAttribute("matchWithScore", matchWithScore);
        req.setAttribute("currentScore", gameState.score());
        req.setAttribute("player1", matchWithScore.match().getFirstPlayer());
        req.setAttribute("player2", matchWithScore.match().getSecondPlayer());
        req.setAttribute("uuid", uuid);
        req.setAttribute("isTieBreak", gameState.isTieBreak());
        req.setAttribute("advantagePlayer", gameState.advantagePlayer());
    }

    public static void setFinishedMatchAttributes(HttpServletRequest req, MatchWithScore matchWithScore, Player winner) {
        Player firstPlayer = matchWithScore.match().getFirstPlayer();
        Player secondPlayer = matchWithScore.match().getSecondPlayer();

        req.setAttribute("winner", winner);
        req.setAttribute("player1", firstPlayer);
        req.setAttribute("player2", secondPlayer);
        req.setAttribute("player1sets", matchWithScore.score().getSets(firstPlayer));
        req.setAttribute("player2sets", matchWithScore.score().getSets(secondPlayer));
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

    public static void setNewMatchAttributes(HttpServletRequest req,
                                             ValidationResult player1Result, ValidationResult player2Result,
                                             String player1Input, String player2Input, String generalError) {
        req.setAttribute("player1Value", player1Input);
        req.setAttribute("player2Value", player2Input);

        if (player1Result.errorMessage() != null) {
            req.setAttribute("player1Error", player1Result.errorMessage());
        }
        if (player2Result.errorMessage() != null) {
            req.setAttribute("player2Error", player2Result.errorMessage());
        }
        if (generalError != null) {
            req.setAttribute("generalError", generalError);
        }
    }
}
