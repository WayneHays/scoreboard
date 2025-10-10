package com.scoreboard.model;

import com.scoreboard.model.entity.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class Score {
    private final Map<Player, PlayerScore> scores;

    @Getter
    @Setter
    private boolean isTieBreak;

    @Getter
    @Setter
    private Player advantage;

    public Score(Player player1, Player player2) {
        this.scores = Map.of(
                player1, new PlayerScore(),
                player2, new PlayerScore()
        );
        this.isTieBreak = false;
        this.advantage = null;
    }

    int getPoints(Player player) {
        return getScore(player).getPoints();
    }

    int getGames(Player player) {
        return getScore(player).getGames();
    }

    int getSets(Player player) {
        return getScore(player).getSets();
    }

    int getTieBreakPoints(Player player) {
        return getScore(player).getTieBreakPoints();
    }

    void awardTennisPoint(Player player) {
        getScore(player).awardPoint();
    }

    void awardTieBreakPoint(Player player) {
        getScore(player).awardTieBreakPoint();
    }

    void awardGame(Player player) {
        getScore(player).awardGame();
    }

    void awardSet(Player player) {
        getScore(player).awardSet();
    }


    void resetAllPoints() {
        scores.values().forEach(PlayerScore::resetPoints);
    }

    void resetAllGames() {
        scores.values().forEach(PlayerScore::resetGames);
    }

    void resetAllTieBreakPoints() {
        scores.values().forEach(PlayerScore::resetTieBreakPoints);
    }

    private PlayerScore getScore(Player player) {
        PlayerScore score = scores.get(player);

        if (score == null) {
            throw new IllegalArgumentException("Player not found: " + player);
        }
        return score;
    }

    private static class PlayerScore {
        private static final int ZERO = 0;
        private static final int FIFTEEN = 15;
        private static final int THIRTY = 30;
        private static final int FORTY = 40;

        private int points = ZERO;
        private int games = ZERO;
        private int sets = ZERO;
        private int tieBreakPoints = ZERO;

        int getPoints() {
            return points;
        }

        int getGames() {
            return games;
        }

        int getSets() {
            return sets;
        }

        int getTieBreakPoints() {
            return tieBreakPoints;
        }

        void awardPoint() {
            points = switch (points) {
                case ZERO -> FIFTEEN;
                case FIFTEEN -> THIRTY;
                case THIRTY -> FORTY;
                default -> points + 1;
            };
        }

        void awardGame() {
            games++;
        }

        void awardSet() {
            sets++;
        }

        void awardTieBreakPoint() {
            tieBreakPoints++;
        }

        void resetPoints() {
            points = ZERO;
        }

        void resetGames() {
            games = ZERO;
        }

        void resetTieBreakPoints() {
            tieBreakPoints = ZERO;
        }
    }
}