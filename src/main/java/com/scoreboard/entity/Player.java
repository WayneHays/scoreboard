package com.scoreboard.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "Players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(
            nullable = false,
            unique = true,
            length = 30,
            columnDefinition = "VARCHAR (30)"
    )

    @EqualsAndHashCode.Include
    private String name;

    public Player(String name) {
        this.name = name;
    }
}


