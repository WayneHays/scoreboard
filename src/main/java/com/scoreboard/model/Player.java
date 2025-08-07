package com.scoreboard.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter

@Entity
@Table(name = "Players")
public class Player {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(
            name = "name",
            nullable = false,
            unique = true,
            columnDefinition = "VARCHAR"
    )
    private String name;

    public Player(String name) {
        this.name = name;
    }
}


