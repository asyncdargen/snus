package ru.dargen.snus.packet.executor;

import lombok.RequiredArgsConstructor;
import ru.dargen.snus.node.RemoteNode;
import ru.dargen.snus.packet.Packet;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@RequiredArgsConstructor
public class ParallelThreadPacketExecutor implements PacketExecutor {

    public final static ThreadFactory THREAD_FACTORY = Thread.ofVirtual().name("Packet-Executor-Thread-", 0).factory();

    private final Executor executor;

    public ParallelThreadPacketExecutor(int threads) {
        this(Executors.newFixedThreadPool(threads, THREAD_FACTORY));
    }

    public ParallelThreadPacketExecutor() {
        this(2);
    }

    @Override
    public void execute(RemoteNode node, Packet packet) {
        executor.execute(() -> node.packetRegistry().firePacketHandlers(packet, node));
    }

}
