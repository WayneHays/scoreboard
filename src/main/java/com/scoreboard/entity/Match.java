package com.scoreboard.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "Matches",
        check = {
                @CheckConstraint(constraint = "firstPlayer != secondPlayer"),
                @CheckConstraint(constraint = "winner = firstPlayer OR winner = secondPlayer")
        }
)
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "first_player", referencedColumnName = "id")
    private Player firstPlayer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "second_player", referencedColumnName = "id")
    private Player secondPlayer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "winner", referencedColumnName = "id")
    private Player winner;

    public Match(Player firstPlayer, Player secondPlayer, Player winner) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.winner = winner;
    }
}
