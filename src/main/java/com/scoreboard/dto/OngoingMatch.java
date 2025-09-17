package com.scoreboard.dto;

import com.scoreboard.model.GameState;
import com.scoreboard.model.Match;
import com.scoreboard.model.Player;
import com.scoreboard.model.Score;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class OngoingMatch {
    private final Match match;
    private final UUID uuid;

    @Setter
    private GameState gameState;


    public OngoingMatch(Match match, GameState gameState, UUID uuid) {
        this.match = match;
        this.uuid = uuid;
        this.gameState = gameState;
    }

    public static OngoingMatch createNew(Match match, Score score, UUID uuid) {
        GameState initialState = new GameState(score, false, null);
        return new OngoingMatch(match, initialState, uuid);
    }

    public Long getFirstPlayerId() {
        return match.getFirstPlayer().getId();
    }

    public Long getSecondPlayerId() {
        return match.getSecondPlayer().getId();
    }

    public String getFirstPlayerName() {
        return match.getFirstPlayer().getName();
    }

    public String getSecondPlayerName() {
        return match.getSecondPlayer().getName();
    }

    public int getFirstPlayerSets() {
        return gameState.score().getSets(match.getFirstPlayer());
    }

    public int getSecondPlayerSets() {
        return gameState.score().getSets(match.getSecondPlayer());
    }

    public int getFirstPlayerGames() {
        return gameState.score().getGames(match.getFirstPlayer());
    }

    public int getSecondPlayerGames() {
        return gameState.score().getGames(match.getSecondPlayer());
    }

    public String getFirstPlayerPoints() {
        return getPlayerPoints(match.getFirstPlayer());
    }

    public String getSecondPlayerPoints() {
        return getPlayerPoints(match.getSecondPlayer());
    }

    private String getPlayerPoints(Player player) {
        if (gameState.advantagePlayer() != null && gameState.advantagePlayer().equals(player)) {
            return "AD";
        }

        if (gameState.isTieBreak()) {
            return String.valueOf(gameState.score().getTieBreakPoints(player));
        }

        return String.valueOf(gameState.score().getPoints(player));
    }

    public String getWinnerName() {
        return match.getWinner() != null ? match.getWinner().getName() : "";
    }

    public int getFirstPlayerFinalSets() {
        return gameState.score().getSets(match.getFirstPlayer());
    }

    public int getSecondPlayerFinalSets() {
        return gameState.score().getSets(match.getSecondPlayer());
    }

    public boolean isFirstPlayerWinner() {
        return match.getWinner() != null
               && match.getWinner().equals(match.getFirstPlayer());
    }

    public boolean isSecondPlayerWinner() {
        return match.getWinner() != null
               && match.getWinner().equals(match.getSecondPlayer());
    }
}
