package ru.dargen.snus.node.server;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import ru.dargen.snus.Snus;
import ru.dargen.snus.configuration.Metrics;
import ru.dargen.snus.configuration.NodeConfiguration;
import ru.dargen.snus.event.NodeEventBus;
import ru.dargen.snus.event.server.ServerBoundEvent;
import ru.dargen.snus.event.server.ServerCloseEvent;
import ru.dargen.snus.event.server.remote.RemoteConnectEvent;
import ru.dargen.snus.eventloop.EventLoop;
import ru.dargen.snus.eventloop.group.EventLoopGroup;
import ru.dargen.snus.node.Node;
import ru.dargen.snus.node.RemoteNode;
import ru.dargen.snus.node.ServerNode;
import ru.dargen.snus.node.base.AbstractNode;
import ru.dargen.snus.packet.PacketRegistry;
import ru.dargen.snus.packet.callback.CallbackProvider;
import ru.dargen.snus.packet.executor.PacketExecutor;
import ru.dargen.snus.util.Collections;
import ru.dargen.snus.util.NioUtil;

import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.logging.Level;

@Getter
@Accessors(fluent = true, chain = true)
public class ServerNodeImpl extends AbstractNode<ServerSocketChannel> implements ServerNode {

    private final EventLoopGroup eventLoopGroup;
    private final Set<RemoteNode> nodes = Collections.newConcurrentSet();

    public ServerNodeImpl(EventLoopGroup eventLoopGroup, EventLoop eventLoop,
                          NodeConfiguration configuration, Metrics metrics,
                          PacketRegistry packetRegistry, PacketExecutor packetExecutor,
                          NodeEventBus eventBus, CallbackProvider callbackProvider) {
        super(eventLoop, configuration, metrics, packetRegistry, packetExecutor, eventBus, callbackProvider);
        this.eventLoopGroup = eventLoopGroup;
    }

    @Override
    @SneakyThrows
    public void bind(SocketAddress address) {
        if (isAlive()) {
            throw new IllegalStateException("Server already bound");
        }

        channel = NioUtil.openServerChannel();
        channel().bind(address);

        Snus.LOGGER.info("Server bound on %s".formatted(getSocketAddress()));

        if (!events().fireResult(new ServerBoundEvent(this))) {
            closeSafe();
            return;
        }

        eventLoop().register(this);
    }

    @Override
    @SneakyThrows
    public RemoteNode accept() {
        SocketChannel channel = null;
        RemoteNode remote = null;
        try {
            channel = channel().accept();
            remote = new ServerRemoteNode(channel, this, eventLoopGroup.take());

            Snus.LOGGER.info("Remote connected %s".formatted(remote.getSocketAddress()));

            if (!events().fireResult(new RemoteConnectEvent(remote, this))) {
                remote.closeSafe();
                return remote;
            }

            remote.eventLoop().register(remote);
            nodes().add(remote);

            return remote;
        } catch (Throwable t) {
            Snus.LOGGER.log(Level.SEVERE, "Error while accepting remote remote", t);

            try {
                if (remote != null) {
                    remote.closeSafe();
                } else if (channel != null) {
                    channel.close();
                }
            } catch (Throwable ignored) {
                //empty
            }
        }

        return null;
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() && !channel().socket().isClosed();
    }

    @Override
    @SneakyThrows
    public void close() {
        nodes.forEach(Node::closeSafe);
        nodes.clear();

        if (isAlive()) {
            Snus.LOGGER.info("Server closed on %s".formatted(getSocketAddress()));
            events().fire(new ServerCloseEvent(this));
            channel.close();
        }
    }
}
