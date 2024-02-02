package ru.dargen.snus.eventloop.exception;

public class EventLoopException extends RuntimeException {

    public EventLoopException(String message) {
        super(message);
    }

    public EventLoopException(String message, Throwable cause) {
        super(message, cause);
    }

}
