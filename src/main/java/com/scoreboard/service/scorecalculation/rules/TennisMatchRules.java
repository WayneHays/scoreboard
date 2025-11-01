package com.scoreboard.service.scorecalculation.rules;

public interface TennisMatchRules {
    GameRules gameRules();
    TiebreakRules tiebreakRules();
    SetRules setRules();
    MatchRules matchRules();
}
