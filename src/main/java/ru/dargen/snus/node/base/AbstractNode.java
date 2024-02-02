package ru.dargen.snus.node.base;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import ru.dargen.snus.attribute.AttributeHolder;
import ru.dargen.snus.attribute.MapAttributeHolder;
import ru.dargen.snus.configuration.Metrics;
import ru.dargen.snus.configuration.NodeConfiguration;
import ru.dargen.snus.event.NodeEventBus;
import ru.dargen.snus.eventloop.EventLoop;
import ru.dargen.snus.node.Node;
import ru.dargen.snus.packet.PacketRegistry;
import ru.dargen.snus.packet.callback.CallbackProvider;
import ru.dargen.snus.packet.executor.PacketExecutor;

import java.nio.channels.SelectableChannel;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true, chain = true)
public abstract class AbstractNode<C extends SelectableChannel> implements Node<C> {

    protected C channel;
    protected final EventLoop eventLoop;
    protected final NodeConfiguration configuration;
    protected final Metrics metrics;
    protected final PacketRegistry packetRegistry;
    protected final PacketExecutor packetExecutor;
    protected final NodeEventBus events;
    protected final CallbackProvider callbackProvider;
    protected final AttributeHolder attributeHolder = new MapAttributeHolder();

    @Override
    public boolean isAlive() {
        return channel != null && channel.isOpen();
    }

    @Override
    @SneakyThrows
    public void close() {
        eventLoop().unregister(this);
        if (isAlive()) {
            channel.close();
        }
    }

}
