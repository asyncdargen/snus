package ru.dargen.snus.node;

import lombok.SneakyThrows;
import ru.dargen.snus.eventloop.group.EventLoopGroup;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public interface ServerNode extends Node<ServerSocketChannel> {

    Set<RemoteNode> nodes();

    EventLoopGroup eventLoopGroup();

    RemoteNode accept();

    void bind(SocketAddress address);

    default CompletableFuture<ServerNode> bindAsync(SocketAddress address) {
        return supplyAsync(() -> {
            try {
                bind(address);
            } catch (Throwable ignored) {
                //empty
            }
            return this;
        });
    }

    default void bind(int port) {
        bind("0.0.0.0", port);
    }

    default CompletableFuture<ServerNode> bindAsync(int port) {
        return bindAsync(new InetSocketAddress("0.0.0.0", port));
    }

    default void bind(String host, int port) {
        bind(new InetSocketAddress(host, port));
    }

    default CompletableFuture<ServerNode> bindAsync(String host, int port) {
        return bindAsync(new InetSocketAddress(host, port));
    }

    @Override
    @SneakyThrows
    default InetSocketAddress getSocketAddress() {
        return (InetSocketAddress) channel().getLocalAddress();
    }

}
