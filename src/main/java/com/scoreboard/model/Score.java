package com.scoreboard.model;

import com.scoreboard.model.entity.Player;

import java.util.Map;

public class Score {
    private final Map<Player, PlayerScore> scores;

    public Score(Player player1, Player player2) {
        this.scores = Map.of(
                player1, new PlayerScore(),
                player2, new PlayerScore()
        );
    }

    public int getPoints(Player player) {
        return getScore(player).getPoints();
    }

    public int getGames(Player player) {
        return getScore(player).getGames();
    }

    public int getSets(Player player) {
        return getScore(player).getSets();
    }

    public int getTieBreakPoints(Player player) {
        return getScore(player).getTieBreakPoints();
    }

    public void awardTennisPoint(Player player) {
        getScore(player).awardPoint();
    }

    public void awardTieBreakPoint(Player player) {
        getScore(player).awardTieBreakPoint();
    }

    public void awardGame(Player player) {
        getScore(player).awardGame();
    }

    public void awardSet(Player player) {
        getScore(player).awardSet();
    }

    public void resetToDeuce() {
        scores.values().forEach(PlayerScore::resetToDeuce);
    }

    public void resetAllPoints() {
        scores.values().forEach(PlayerScore::resetPoints);
    }

    public void resetAllGames() {
        scores.values().forEach(PlayerScore::resetGames);
    }

    public void resetAllTieBreakPoints() {
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

        void resetToDeuce() {
            points = FORTY;
        }
    }
}