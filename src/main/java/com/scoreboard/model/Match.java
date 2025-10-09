package com.scoreboard.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "Matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    private Long id;

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne
    @JoinColumn(name = "firstPlayer", referencedColumnName = "id")
    private Player firstPlayer;

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne
    @JoinColumn(name = "secondPlayer", referencedColumnName = "id")
    private Player secondPlayer;

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne
    @JoinColumn(name = "winner", referencedColumnName = "id")
    private Player winner;

    public Match(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    public Match(Player firstPlayer, Player secondPlayer, Player winner) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.winner = winner;
    }
}
