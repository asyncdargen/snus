package ru.dargen.snus.configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import ru.dargen.snus.buffer.BufferPool;
import ru.dargen.snus.node.RemoteNode;


@Getter
@Builder
@Accessors(fluent = true, chain = true)
public class NodeConfiguration {

    @Builder.Default
    private final int packetBufferSize = 1 << 23;
    @Builder.Default
    private final int bufferPoolSize = 1024;

    @Builder.Default
    private final boolean tcpNoDelay = true;
    @Builder.Default
    private final boolean keepAlive = true;
    @Builder.Default
    private final int soTimeout = 30_000;

    private BufferPool bufferPool;

    public BufferPool bufferPool() {
        return bufferPool == null ? bufferPool = new BufferPool(bufferPoolSize, packetBufferSize) : bufferPool;
    }

    @SneakyThrows
    public void configure(RemoteNode node) {
        var socket = node.channel().socket();
        socket.setSendBufferSize(Integer.MAX_VALUE);
        socket.setReceiveBufferSize(Integer.MAX_VALUE);

        socket.setTcpNoDelay(tcpNoDelay);
        socket.setSoTimeout(1000);
        socket.setKeepAlive(keepAlive);
    }

}
