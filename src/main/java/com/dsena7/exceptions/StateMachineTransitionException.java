package com.dsena7.exceptions;

public class StateMachineTransitionException extends RuntimeException {
    public StateMachineTransitionException(String message) {
        super(message);
    }

    public StateMachineTransitionException(String message, Throwable cause) {
        super(message, cause);
    }
}