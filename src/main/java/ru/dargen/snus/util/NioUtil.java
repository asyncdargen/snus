package ru.dargen.snus.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@UtilityClass
public class NioUtil {

    @SneakyThrows
    public Selector selector() {
        return Selector.open();
    }

    @SneakyThrows
    public ServerSocketChannel openServerChannel() {
        return ServerSocketChannel.open();
    }

    @SneakyThrows
    public SocketChannel openChannel() {
        return SocketChannel.open();
    }

    @SneakyThrows
    public InetSocketAddress resolveAddress(InetSocketAddress address) {
        return new InetSocketAddress(address.getAddress().getHostAddress(), address.getPort());
    }

}
