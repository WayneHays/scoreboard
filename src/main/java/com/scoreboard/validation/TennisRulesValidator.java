package com.scoreboard.validation;

import com.scoreboard.exception.ValidationException;
import com.scoreboard.tennisrules.MatchRules;
import com.scoreboard.tennisrules.SetRules;
import com.scoreboard.tennisrules.TennisMatchRules;
import com.scoreboard.tennisrules.TiebreakRules;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TennisRulesValidator {
    private static final Logger logger = LoggerFactory.getLogger(TennisRulesValidator.class);
    private static final int MIN_TIEBREAK_POINTS = 3;
    private static final int MAX_TIEBREAK_POINTS = 21;
    private static final int MIN_GAMES_TO_WIN = 2;
    private static final int MAX_GAMES_TO_WIN = 12;
    private static final int MIN_SETS_TO_WIN = 1;
    private static final int MAX_SETS_TO_WIN = 5;

    public static void validate(TennisMatchRules rules) {
        validateTiebreakRules(rules.tiebreakRules());
        validateSetRules(rules.setRules());
        validateMatchRules(rules.matchRules());
    }

    private static void validateTiebreakRules(TiebreakRules rules) {
        int points = rules.pointsToWinTieBreak();

        if (points < MIN_TIEBREAK_POINTS || points > MAX_TIEBREAK_POINTS) {
            throw new ValidationException(
                    String.format(
                            "Tiebreak points must be between %d and %d, got: %d",
                            MIN_TIEBREAK_POINTS, MAX_TIEBREAK_POINTS, points
                    )
            );
        }
    }

    private static void validateSetRules(SetRules rules) {
        int games = rules.gamesToWinSet();

        if (games < MIN_GAMES_TO_WIN || games > MAX_GAMES_TO_WIN) {
            throw new ValidationException(
                    String.format(
                            "Games to win must be between %d and %d, got: %d",
                            MIN_GAMES_TO_WIN, MAX_GAMES_TO_WIN, games
                    )
            );
        }
    }

    private static void validateMatchRules(MatchRules rules) {
        int sets = rules.setsToWinMatch();

        if (sets < MIN_SETS_TO_WIN || sets > MAX_SETS_TO_WIN) {
            throw new ValidationException(
                    String.format(
                            "Sets to win must be between %d and %d, got: %d",
                            MIN_SETS_TO_WIN, MAX_SETS_TO_WIN, sets
                    )
            );
        }
    }
}
