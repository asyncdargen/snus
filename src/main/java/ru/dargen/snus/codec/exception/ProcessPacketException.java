package ru.dargen.snus.codec.exception;

public class ProcessPacketException extends RuntimeException {

    public ProcessPacketException(String message) {
        super(message);
    }

    public ProcessPacketException(String message, Throwable cause) {
        super(message, cause);
    }

}
