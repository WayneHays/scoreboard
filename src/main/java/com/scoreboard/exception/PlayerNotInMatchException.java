package com.scoreboard.exception;

public class PlayerNotInMatchException extends RuntimeException {

    public PlayerNotInMatchException(String message) {
        super(message);
    }
}
