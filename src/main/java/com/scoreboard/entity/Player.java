package com.scoreboard.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Player {
    private Long id;
    private String name;

    public Player(String name) {
        this.name = name;
    }
}
