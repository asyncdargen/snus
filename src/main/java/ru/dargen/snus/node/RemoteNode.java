package ru.dargen.snus.node;

import lombok.SneakyThrows;
import ru.dargen.snus.packet.Packet;
import ru.dargen.snus.packet.callback.Callback;
import ru.dargen.snus.util.NioUtil;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.UUID;

public interface RemoteNode extends Node<SocketChannel> {

    void write(UUID uuid, Packet packet);

    default void write(Packet packet) {
        write(packet.getUniqueId(), packet);
    }

    default void writeAndFlush(UUID uuid, Packet packet) {
        write(uuid, packet);
        flushOut();
    }

    default void writeAndFlush(Packet packet) {
        writeAndFlush(packet.getUniqueId(), packet);
    }

    <P extends Packet> Callback<P> writeCallback(UUID uuid, Packet packet);

    default <P extends Packet> Callback<P> writeCallback(Packet packet) {
        return writeCallback(packet.getUniqueId(), packet);
    }

    default <P extends Packet> Callback<P> writeCallbackAndFlush(UUID uuid, Packet packet) {
        try {
            return writeCallback(uuid, packet);
        } finally {
            flushOut();
        }
    }

    default <P extends Packet> Callback<P> writeCallbackAndFlush(Packet packet) {
        return writeCallbackAndFlush(packet.getUniqueId(), packet);
    }

    default void respond(Packet respondPacket, Packet packet) {
        write(respondPacket.getUniqueId(), packet);
    }

    default void respondAndFlush(Packet respondPacket, Packet packet) {
        writeAndFlush(respondPacket.getUniqueId(), packet);
    }

    default <P extends Packet> Callback<P> respondCallback(Packet respondPacket, Packet packet) {
        return writeCallback(respondPacket.getUniqueId(), packet);
    }

    default <P extends Packet> Callback<P> respondCallbackAndFlush(Packet respondPacket, Packet packet) {
        return writeCallbackAndFlush(respondPacket.getUniqueId(), packet);
    }

    void flushOut();

    void flushIn();

    @Override
    @SneakyThrows
    default InetSocketAddress getSocketAddress() {
        return NioUtil.resolveAddress((InetSocketAddress) channel().getRemoteAddress());
    }

}
