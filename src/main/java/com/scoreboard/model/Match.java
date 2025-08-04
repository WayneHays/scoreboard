package com.scoreboard.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data

@Entity
@Table(name = "Matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "firstPlayer", referencedColumnName = "id")
    private Player firstPlayer;

    @ManyToOne
    @JoinColumn(name = "secondPlayer", referencedColumnName = "id")
    private Player secondPlayer;

    @ManyToOne
    @JoinColumn(name = "winner", referencedColumnName = "id")
    private Player winner;

    public Match(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }
}
