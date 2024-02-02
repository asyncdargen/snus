package ru.dargen.snus.node;

import lombok.SneakyThrows;
import ru.dargen.snus.eventloop.group.EventLoopGroup;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

public interface ServerNode extends Node<ServerSocketChannel> {

    Set<RemoteNode> nodes();

    EventLoopGroup eventLoopGroup();

    RemoteNode accept();

    void bind(SocketAddress address);

    default void bind(int port) {
        bind("0.0.0.0", port);
    }

    default void bind(String host, int port) {
        bind(new InetSocketAddress(host, port));
    }

    @Override
    @SneakyThrows
    default InetSocketAddress getSocketAddress() {
        return (InetSocketAddress) channel().getLocalAddress();
    }

}
