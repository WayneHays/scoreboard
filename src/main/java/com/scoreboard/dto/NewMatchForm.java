package com.scoreboard.dto;

import lombok.Builder;

@Builder
public record NewMatchForm(
        String player1Value,
        String player2Value,
        String player1Error,
        String player2Error,
        String generalError
) {

    public boolean hasPlayer1Error() {
        return player1Error != null && !player1Error.trim().isEmpty();
    }

    public boolean hasPlayer2Error() {
        return player2Error != null && !player2Error.trim().isEmpty();
    }

    public boolean hasGeneralError() {
        return generalError != null && !generalError.trim().isEmpty();
    }

    public boolean hasAnyError() {
        return hasPlayer1Error() || hasPlayer2Error() || hasGeneralError();
    }
}
