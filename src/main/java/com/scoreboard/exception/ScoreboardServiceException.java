package com.scoreboard.exception;

public class ScoreboardServiceException extends RuntimeException{

    public ScoreboardServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
