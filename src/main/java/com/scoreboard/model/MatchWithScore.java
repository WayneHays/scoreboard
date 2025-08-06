package com.scoreboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter

public class MatchWithScore {
    private Match match;
    private Score score;
}
