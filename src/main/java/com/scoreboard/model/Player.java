package com.scoreboard.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Setter
@Getter

@Entity
@Table (name = "Players")
public class Player {
    @Id
    @Column (name = "id")
    private Long id;

    @Column (name = "name")
    private String name;

    public Player(String name) {
        this.name = name;
    }
}
