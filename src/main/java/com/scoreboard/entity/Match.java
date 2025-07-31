package com.scoreboard.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Match {
    private Long id;
    private Long firstPlayerId;
    private Long secondPlayerId;
    private Long winnerId;

    public Match(Long firstPlayerId, Long secondPlayerId, Long winnerId) {
        this.firstPlayerId = firstPlayerId;
        this.secondPlayerId = secondPlayerId;
        this.winnerId = winnerId;
    }
}
