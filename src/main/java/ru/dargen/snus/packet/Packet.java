package ru.dargen.snus.packet;

import lombok.Setter;
import lombok.val;
import ru.dargen.snus.buffer.Buffer;

import java.util.UUID;

public abstract class Packet {

    @Setter
    protected UUID uniqueId;

    public UUID getUniqueId() {
        return uniqueId == null ? (uniqueId = UUID.randomUUID()) : uniqueId;
    }

    public void write(Buffer buffer) {

    }

    public void read(Buffer buffer) {

    }

    @Override
    public Packet clone() {
        try {
            return (Packet) super.clone();
        } catch (CloneNotSupportedException ignored) {
            return null;
        }
    }

    public Packet cloneEraseId() {
        val clone = clone();
        clone.setUniqueId(null);
        return clone;
    }

}
