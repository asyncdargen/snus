package ru.dargen.snus.codec;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import ru.dargen.snus.Snus;
import ru.dargen.snus.buffer.Buffer;
import ru.dargen.snus.buffer.exception.BufferReadException;
import ru.dargen.snus.node.RemoteNode;
import ru.dargen.snus.packet.Packet;
import ru.dargen.snus.util.Collections;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.logging.Level;

@Getter
@RequiredArgsConstructor
@ExtensionMethod({Collections.class})
@Accessors(chain = true, fluent = true)
public class VarInt21FrameMessageProcessor {

    protected final RemoteNode node;

    protected final Queue<Consumer<Buffer>> queue = new ArrayDeque<>();
    private ByteBuffer remainingFrame;

    public void enqueue(Consumer<Buffer> writer) {
        synchronized (queue) {
            queue.add(writer);
        }
    }

//    public void enqueue(Buffer buffer) {
//        enqueue(out -> out.writeBuffer(out));
//    }

    public void enqueue(Packet packet) {
        enqueue(out -> PacketProcessor.processOutPacket(node, packet, out));
    }

    public void write() throws IOException {
        synchronized (queue) {
            if (queue.isEmpty()) return;
            try (var holder = node.configuration().bufferPool().acquire()) {
                var buffer = holder.buffer();
                queue.drain(frame -> {
                    var startIndex = buffer.shiftWrite(4);
                    try {
                        frame.accept(buffer);

                        var length = buffer.writeIndex() - startIndex - 4;
                        buffer.writeIndex(startIndex);
                        buffer.writeInt(length);
                        buffer.shiftWrite(length);

                        node.metrics().incrementOutPackets();
                    } catch (Throwable t) {
                        Snus.LOGGER.log(Level.SEVERE, "Error while packet writing", t);

                        buffer.writeIndex(startIndex);
                    }
                });

                if (node.channel().write(buffer.slice(0, buffer.writeIndex()).buffer()) < 0) {
                    throw new IOException("Channel closed");
                }
                node.metrics().incrementOutBytes(buffer.readableBytes());
            }
        }
    }

    public void read() throws IOException {
        try (var holder = node.configuration().bufferPool().acquire()) {
            var buffer = holder.buffer();

            if (remainingFrame != null) {
                buffer.writeBuffer(remainingFrame);

                remainingFrame = null;
            }

            var bytes = buffer.readChannel(node.channel());
            node.metrics().incrementInBytes(bytes);

            remainingFrame = processFrames(buffer);
        }
    }

    private ByteBuffer processFrames(Buffer buffer) {
        while (buffer.isReadable()) {
            var readerIndex = buffer.readIndex();
            try {
                var length = buffer.readInt();

                if (length > buffer.readableBytes() || length <= 0) {
                    throw new BufferReadException(length, buffer);
                }

                var frame = buffer.slice(buffer.readIndex(), length).writeIndex(length);
                buffer.shiftRead(length);
                try {
                    node.metrics().incrementInPackets();
                    PacketProcessor.processInPacket(node, frame);
                } catch (Throwable t) {
                    Snus.LOGGER.log(Level.SEVERE, "Error while packet processing", t);
                }
            } catch (Throwable exception) {
                return buffer
                        .buffer()
                        .slice(readerIndex, buffer.writeIndex() - readerIndex)
                        .duplicate();
            }
        }

        return null;
    }

    public void reset() {
        remainingFrame = null;
        queue.clear();
    }

}
