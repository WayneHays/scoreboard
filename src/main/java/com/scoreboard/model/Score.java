package com.scoreboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Score {
    private int firstPlayerPoints;
    private int firstPlayerGames;
    private int firstPlayerSets;
    private int secondPlayerPoints;
    private int secondPlayerGames;
    private int secondPlayerSets;
}