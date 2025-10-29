package com.scoreboard.service.scorecalculation.rules;

import com.scoreboard.service.scorecalculation.rules.standart.StandardGameRules;
import com.scoreboard.service.scorecalculation.rules.standart.StandardMatchRules;
import com.scoreboard.service.scorecalculation.rules.standart.StandardSetRules;
import com.scoreboard.service.scorecalculation.rules.standart.StandardTiebreakRules;

import java.util.Objects;

public record TennisMatchRules(
        GameRules gameRules,
        SetRules setRules,
        MatchRules matchRules,
        TiebreakRules tiebreakRules) {

    public TennisMatchRules {
        Objects.requireNonNull(gameRules, "GameRules cannot be null");
        Objects.requireNonNull(setRules, "SetRules cannot be null");
        Objects.requireNonNull(matchRules, "MatchRules cannot be null");
        Objects.requireNonNull(tiebreakRules, "TiebreakRules cannot be null");
    }

    public static TennisMatchRules standard() {
        return new TennisMatchRules(
                new StandardGameRules(),
                new StandardSetRules(),
                new StandardMatchRules(),
                new StandardTiebreakRules()
        );
    }

    public static TennisMatchRules custom(
            GameRules gameRules,
            SetRules setRules,
            MatchRules matchRules,
            TiebreakRules tiebreakRules) {
        return new TennisMatchRules(gameRules, setRules, matchRules, tiebreakRules);
    }
}
