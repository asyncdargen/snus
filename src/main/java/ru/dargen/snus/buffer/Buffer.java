package ru.dargen.snus.buffer;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import ru.dargen.snus.buffer.exception.BufferReadException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Setter
@Getter
@Accessors(chain = true, fluent = true)
public class Buffer {

    public static Buffer create() {
        return create(1024);
    }

    public static Buffer create(int size) {
        return create(ByteBuffer.allocateDirect(size));
    }

    public static Buffer create(ByteBuffer buffer) {
        return new Buffer(buffer);
    }

    protected ByteBuffer buffer;
    protected int writeIndex, readIndex;

    public Buffer(ByteBuffer buffer) {
        this.buffer = buffer;

        this.writeIndex = buffer.position();
        this.readIndex = buffer.position();
    }

    public Buffer writeByte(byte value) {
        ensureSize(1);
        buffer.put(shiftWrite(1), value);
        return this;
    }

    public byte readByte() {
        checkSize(1);
        return buffer.get(shiftRead(1));
    }

    public Buffer writeBoolean(boolean value) {
        return writeByte((byte) (value ? 1 : 0));
    }

    public boolean readBoolean() {
        return readByte() == 1;
    }

    public Buffer writeShort(short value) {
        ensureSize(2);
        buffer.putShort(shiftWrite(2), value);
        return this;
    }

    public short readShort() {
        checkSize(2);
        return buffer.getShort(shiftRead(2));
    }

    public Buffer writeChar(char value) {
        ensureSize(2);
        buffer.putChar(shiftWrite(2), value);
        return this;
    }

    public char readChar() {
        checkSize(2);
        return buffer.getChar(shiftRead(2));
    }

    public Buffer writeInt(int value) {
        ensureSize(4);
        buffer.putInt(shiftWrite(4), value);
        return this;
    }

    public int readInt() {
        checkSize(4);
        return buffer.getInt(shiftRead(4));
    }

    public Buffer writeLong(long value) {
        ensureSize(8);
        buffer.putLong(shiftWrite(8), value);
        return this;
    }

    public long readLong() {
        checkSize(8);
        return buffer.getLong(shiftRead(8));
    }

    public Buffer writeFloat(float value) {
        ensureSize(4);
        buffer.putFloat(shiftWrite(4), value);
        return this;
    }

    public float readFloat() {
        checkSize(4);
        return buffer.getFloat(shiftRead(4));
    }

    public Buffer writeDouble(double value) {
        ensureSize(8);
        buffer.putDouble(shiftWrite(8), value);
        return this;
    }

    public double readDouble() {
        checkSize(8);
        return buffer.getDouble(shiftRead(8));
    }

    public Buffer writeVarInt(int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                writeByte((byte) value);
                return this;
            }

            writeByte((byte) ((value & 0x7F) | 0x80));

            value >>>= 7;
        }
    }

    public int readVarInt() {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = readByte();
            value |= (currentByte & 0x7F) << position;

            if ((currentByte & 0x80) == 0) break;

            position += 7;

            if (position >= 32) throw new BufferReadException("VarInt is too big");
        }

        return value;
    }

    public Buffer writeVarLong(long value) {
        while (true) {
            if ((value & ~((long) 0x7F)) == 0) {
                writeByte((byte) value);
                return this;
            }

            writeByte((byte) ((value & 0x7F) | 0x80));

            value >>>= 7;
        }
    }

    public long readVarLong() {
        long value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = readByte();
            value |= (long) (currentByte & 0x7F) << position;

            if ((currentByte & 0x80) == 0) break;

            position += 7;

            if (position >= 64) throw new RuntimeException("VarLong is too big");
        }

        return value;
    }

    public Buffer writeUUID(UUID value) {
        ensureSize(16);
        writeLong(value.getMostSignificantBits());
        writeLong(value.getLeastSignificantBits());

        return this;
    }

    public UUID readUUID() {
        checkSize(16);
        return new UUID(readLong(), readLong());
    }

    public Buffer writeEnum(Enum<?> value) {
        writeVarInt(value.ordinal());

        return this;
    }

    public <E extends Enum<E>> E readEnum(Class<E> enumClass) {
        return enumClass.getEnumConstants()[readVarInt()];
    }

    public Buffer writeBuffer(ByteBuffer buffer, int start, int length) {
        ensureSize(length);
        this.buffer.put(shiftWrite(length), buffer, start, length);

        return this;
    }

    public Buffer writeBuffer(ByteBuffer buffer) {
        return writeBuffer(buffer, 0, buffer.limit());
    }

    public Buffer writeBuffer(Buffer buffer, int start, int length) {
        return writeBuffer(buffer.buffer, start, length);
    }

    public Buffer writeBuffer(Buffer buffer) {
        return writeBuffer(buffer, buffer.readIndex, buffer.readableBytes());
    }

    public Buffer writeBytes(byte[] bytes, int start, int length) {
        ensureSize(length);
        buffer.put(shiftWrite(length), bytes, start, length);

        return this;
    }

    public Buffer writeBytes(byte[] bytes) {
        return writeBytes(bytes, 0, bytes.length);
    }

    public byte[] readBytes(byte[] bytes) {
        checkSize(bytes.length);
        buffer.get(shiftRead(bytes.length), bytes);
        return bytes;
    }

    public byte[] readBytes(int length) {
        return readBytes(new byte[length]);
    }

    public Buffer writeByteArray(byte[] value) {
        writeVarInt(value.length);
        writeBytes(value);

        return this;
    }

    public byte[] readByteArray() {
        var length = readVarInt();
        return readBytes(length);
    }

    public Buffer writeString(String value) {
        writeByteArray(value.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    public String readString() {
        return new String(readByteArray(), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public int readChannel(SocketChannel channel) {
        int length = channel.read(buffer.slice(writeIndex(), writableBytes()));

        if (length < 0) {
            throw new IOException("EOS");
        }

        shiftWrite(length);

        return length;
    }


    public Buffer slice(int index, int length) {
        return new Buffer(buffer.slice(index, length));
    }

    public Buffer flip() {
        buffer.flip();
        return this;
    }

    public void clear() {
        buffer.clear();
        this.writeIndex = 0;
        this.readIndex = 0;
    }

    public int position() {
        return buffer.position();
    }

    public Buffer position(int position) {
        buffer.position(position);
        return this;
    }

    public int shiftWrite(int length) {
        final int oldWriteIndex = writeIndex;
        writeIndex += length;
        return oldWriteIndex;
    }

    public int shiftRead(int length) {
        int oldReadIndex = readIndex;
        readIndex += length;
        return oldReadIndex;
    }

    public int writableBytes() {
        return buffer.capacity() - writeIndex;
    }

    public int readableBytes() {
        return writeIndex - readIndex;
    }

    public boolean isWritable() {
        return writableBytes() > 0;
    }

    public boolean isReadable() {
        return readableBytes() > 0;
    }

    void ensureSize(int length) {
        if (buffer.capacity() < writeIndex + length) {
            int newCapacity = Math.max(buffer.capacity() * 2, writeIndex + length);
            buffer = ByteBuffer.allocateDirect(newCapacity)
                    .put(buffer.position(0)) //copy old
                    .clear();
            throw new RuntimeException();
        }
    }

    void checkSize(int length) {
        if (readIndex + length > writeIndex) {
            throw new BufferReadException(length, this);
        }
    }

}

