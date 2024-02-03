package ru.dargen.snus.node.client;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import ru.dargen.snus.Snus;
import ru.dargen.snus.configuration.Metrics;
import ru.dargen.snus.configuration.NodeConfiguration;
import ru.dargen.snus.event.NodeEventBus;
import ru.dargen.snus.event.client.ClientConnectEvent;
import ru.dargen.snus.event.client.ClientDisconnectEvent;
import ru.dargen.snus.event.client.ClientReconnectEvent;
import ru.dargen.snus.eventloop.EventLoop;
import ru.dargen.snus.node.ClientNode;
import ru.dargen.snus.node.base.AbstractRemoteNode;
import ru.dargen.snus.packet.PacketRegistry;
import ru.dargen.snus.packet.callback.CallbackProvider;
import ru.dargen.snus.packet.executor.PacketExecutor;
import ru.dargen.snus.util.NioUtil;

import java.net.InetSocketAddress;
import java.util.logging.Level;

@Getter
@Setter
public class ClientRemoteNode extends AbstractRemoteNode implements ClientNode {

    private boolean autoReconnect = true;

    @Accessors(fluent = false)
    protected InetSocketAddress socketAddress;

    public ClientRemoteNode(EventLoop eventLoop, NodeConfiguration configuration, Metrics metrics,
                            PacketRegistry packetRegistry, PacketExecutor packetExecutor,
                            NodeEventBus eventBus, CallbackProvider callbackProvider) {
        super(eventLoop, configuration, metrics, packetRegistry, packetExecutor, eventBus, callbackProvider);
    }

    @Override
    public void tryReconnect() {
        if (autoReconnect) {
            Snus.LOGGER.info("Trying reconnect client");
            eventLoop.execute(this::reconnect);
        }
    }

    @Override
    public void reconnect() {
        if (isAlive()) return;

        if (events().fireResult(new ClientReconnectEvent(this, autoReconnect))) {
            connect(getSocketAddress());
        }
    }

    @Override
    @SneakyThrows
    public void connect(InetSocketAddress address) {
        socketAddress = NioUtil.resolveAddress(address);

        if (isAlive()) {
            throw new IllegalStateException("Client already connected");
        }

        try {
            channel = NioUtil.openChannel();
            channel.connect(address);
        } catch (Throwable t) {
            Snus.LOGGER.log(Level.SEVERE, "Error while connecting %s".formatted(getSocketAddress()), t);
            closeSafe();
            return;
        }

        Snus.LOGGER.info("Successful connected to %s".formatted(getSocketAddress()));

        if (!events().fireResult(new ClientConnectEvent(this))) {
            closeSafe();
            return;
        }

        configuration().configure(this);
        eventLoop.register(this);
    }

    @Override
    public void close() {
        if (isAlive()) {
            Snus.LOGGER.info("Client disconnected from %s".formatted(getSocketAddress()));
            events().fire(new ClientDisconnectEvent(this));
        }

        super.close();
        tryReconnect();
    }

}
