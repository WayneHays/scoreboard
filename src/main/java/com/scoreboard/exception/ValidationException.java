package com.scoreboard.exception;

public class ValidationException extends ScoreboardServiceException{

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
