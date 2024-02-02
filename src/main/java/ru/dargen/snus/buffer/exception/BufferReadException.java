package ru.dargen.snus.buffer.exception;

import ru.dargen.snus.buffer.Buffer;

public class BufferReadException extends BufferException {

    public BufferReadException(String message) {
        super(message);
    }

    public BufferReadException(int length, int readerIndex, int writerIndex) {
        this("(ln: %s, rx: %s, wx: %s, mg: %s)"
                .formatted(length, readerIndex, writerIndex, writerIndex - readerIndex - length));
    }

    public BufferReadException(int length, Buffer buffer) {
        this(length, buffer.readIndex(), buffer.writeIndex());
    }

}
