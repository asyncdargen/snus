package ru.dargen.snus.node;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public interface ClientNode extends RemoteNode {

    boolean isAutoReconnect();

    void setAutoReconnect(boolean autoReconnect);

    void tryReconnect();

    void reconnect();

    void connect(InetSocketAddress address);

    default CompletableFuture<ClientNode> connectAsync(InetSocketAddress address) {
        return supplyAsync(() -> {
            try {
                connect(address);
            } catch (Throwable ignored) {
                //empty
            }
            return this;
        });
    }

    default void connect(String host, int port) {
        connect(new InetSocketAddress(host, port));
    }

    default CompletableFuture<ClientNode> connectAsync(String host, int port) {
        return connectAsync(new InetSocketAddress(host, port));
    }

    default void connect(int port) {
        connect("0.0.0.0", port);
    }

    default CompletableFuture<ClientNode> connectAsync(int port) {
        return connectAsync("localhost", port);
    }

    default void close(boolean disableReconnect) {
        if (disableReconnect) {
            setAutoReconnect(false);
        }

        close();
    }

}
