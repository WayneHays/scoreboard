package com.scoreboard.tennisrules;

public interface TennisRules {
    GameRules gameRules();
    TiebreakRules tiebreakRules();
    SetRules setRules();
    MatchRules matchRules();
}
