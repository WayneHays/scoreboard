package com.scoreboard.tennisrules;

public interface TennisMatchRules {
    GameRules gameRules();
    TiebreakRules tiebreakRules();
    SetRules setRules();
    MatchRules matchRules();
}
