package ru.dargen.snus.node;

import ru.dargen.snus.attribute.DelegateAttributeHolder;
import ru.dargen.snus.configuration.NodeConfiguration;
import ru.dargen.snus.event.NodeEventBus;
import ru.dargen.snus.eventloop.EventLoop;
import ru.dargen.snus.configuration.Metrics;
import ru.dargen.snus.packet.PacketRegistry;
import ru.dargen.snus.packet.callback.CallbackProvider;
import ru.dargen.snus.packet.executor.PacketExecutor;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;

public interface Node<C extends SelectableChannel> extends DelegateAttributeHolder {

    C channel();

    EventLoop eventLoop();

    NodeConfiguration configuration();

    Metrics metrics();

    PacketRegistry packetRegistry();

    PacketExecutor packetExecutor();

    NodeEventBus events();

    CallbackProvider callbackProvider();

    InetSocketAddress getSocketAddress();

    default InetAddress getAddress() {
        return getSocketAddress().getAddress();
    }

    boolean isAlive();

    void close();

    default void closeSafe() {
        try {
            close();
        } catch (Throwable ignored) {
            //empty
        }
    }

}
