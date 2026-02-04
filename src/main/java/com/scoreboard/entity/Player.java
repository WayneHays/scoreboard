package com.scoreboard.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "Players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Long id;

    @Column(
            name = "name",
            nullable = false,
            unique = true,
            columnDefinition = "VARCHAR"
    )

    @EqualsAndHashCode.Include
    private String name;

    public Player(String name) {
        this.name = name;
    }
}


