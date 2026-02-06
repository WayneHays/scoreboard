package com.scoreboard.exception;

public class ApplicationStartupException extends RuntimeException {

    public ApplicationStartupException(String message, Throwable cause) {
        super(message, cause);
    }
}
