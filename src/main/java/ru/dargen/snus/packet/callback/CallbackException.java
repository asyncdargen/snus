package ru.dargen.snus.packet.callback;

import java.util.UUID;

public class CallbackException extends RuntimeException {

    public CallbackException(String message) {
        super(message);
    }

    public CallbackException(String message, Throwable cause) {
        super(message, cause);
    }

    public CallbackException(UUID id, String message, Throwable cause) {
        this(message + " " + id, cause);
    }

}
