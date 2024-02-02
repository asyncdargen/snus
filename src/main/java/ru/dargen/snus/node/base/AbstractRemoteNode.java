package ru.dargen.snus.node.base;

import lombok.SneakyThrows;
import ru.dargen.snus.codec.VarInt21FrameMessageProcessor;
import ru.dargen.snus.configuration.Metrics;
import ru.dargen.snus.configuration.NodeConfiguration;
import ru.dargen.snus.event.NodeEventBus;
import ru.dargen.snus.eventloop.EventLoop;
import ru.dargen.snus.node.RemoteNode;
import ru.dargen.snus.packet.Packet;
import ru.dargen.snus.packet.PacketRegistry;
import ru.dargen.snus.packet.callback.Callback;
import ru.dargen.snus.packet.callback.CallbackProvider;
import ru.dargen.snus.packet.executor.PacketExecutor;

import java.nio.channels.SocketChannel;
import java.util.UUID;

public abstract class AbstractRemoteNode extends AbstractNode<SocketChannel> implements RemoteNode {

    protected final VarInt21FrameMessageProcessor frameMessageProcessor = new VarInt21FrameMessageProcessor(this);

    public AbstractRemoteNode(
            EventLoop eventLoop,
            NodeConfiguration configuration, Metrics metrics,
            PacketRegistry packetRegistry, PacketExecutor packetExecutor,
            NodeEventBus eventBus, CallbackProvider callbackProvider
    ) {
        super(eventLoop, configuration, metrics, packetRegistry, packetExecutor, eventBus, callbackProvider);
    }

    @Override
    public void write(UUID uuid, Packet packet) {
        if (!isAlive()) return;

        packet.setUniqueId(uuid);
        frameMessageProcessor.enqueue(packet);
    }

    @Override
    public <P extends Packet> Callback<P> writeCallback(UUID uuid, Packet packet) {
        write(uuid, packet);
        return callbackProvider().create(packet, this);
    }

    @Override
    @SneakyThrows
    public void flushOut() {
        frameMessageProcessor.write();
    }

    @Override
    @SneakyThrows
    public void flushIn() {
        frameMessageProcessor.read();
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() && !channel.socket().isClosed();
    }

    @Override
    public void close() {
        frameMessageProcessor.reset();
        super.close();
    }

}
