package org.irlab.model.exceptions;

public class InvalidInputException extends Exception {

    public InvalidInputException() {
        super("Datos de entrada no válidos.");
    }

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputException(Throwable cause) {
        super(cause);
    }
}

