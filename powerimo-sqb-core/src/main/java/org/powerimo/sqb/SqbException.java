package org.powerimo.sqb;

public class SqbException extends RuntimeException {
    public SqbException() {
        super();
    }

    public SqbException(String message) {
        super(message);
    }

    public SqbException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqbException(Throwable cause) {
        super(cause);
    }
}
