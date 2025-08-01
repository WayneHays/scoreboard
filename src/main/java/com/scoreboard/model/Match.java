package com.scoreboard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter

@Entity
@Table(name = "Matches")
public class Match {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "player1")
    private Long firstPlayerId;

    @Column(name = "player2")
    private Long secondPlayerId;

    @Column(name = "winner")
    private Long winnerId;

    public Match(Long firstPlayerId, Long secondPlayerId, Long winnerId) {
        this.firstPlayerId = firstPlayerId;
        this.secondPlayerId = secondPlayerId;
        this.winnerId = winnerId;
    }
}
