package com.scoreboard.domain.rules;

public record TennisRules(MatchRules matchRules,
                          SetRules setRules,
                          GameRules gameRules,
                          TiebreakRules tiebreakRules) {
}
