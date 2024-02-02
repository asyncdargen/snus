package ru.dargen.snus.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.dargen.snus.configuration.Metrics;
import ru.dargen.snus.configuration.NodeConfiguration;
import ru.dargen.snus.event.NodeEventBus;
import ru.dargen.snus.eventloop.EventLoop;
import ru.dargen.snus.eventloop.factory.EventLoopFactory;
import ru.dargen.snus.eventloop.factory.RemoteEventLoopFactory;
import ru.dargen.snus.eventloop.factory.ServerEventLoopFactory;
import ru.dargen.snus.eventloop.group.EventLoopGroup;
import ru.dargen.snus.eventloop.group.SimpleEventLoopGroup;
import ru.dargen.snus.node.ServerNode;
import ru.dargen.snus.node.server.ServerNodeImpl;
import ru.dargen.snus.packet.PacketRegistry;
import ru.dargen.snus.packet.callback.CallbackProvider;
import ru.dargen.snus.packet.executor.PacketExecutor;
import ru.dargen.snus.packet.executor.ThreadInPacketExecutor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class ServerBuilder {

    private EventLoop eventLoop;
    private EventLoopFactory eventLoopFactory = ServerEventLoopFactory.INSTANCE;

    private EventLoopGroup remoteEventLoopGroup;
    private EventLoopFactory remoteEventLoopFactory = RemoteEventLoopFactory.INSTANCE;
    private int remoteEventLoopGroupSize = 4;

    private NodeConfiguration configuration = NodeConfiguration.builder().build();
    private Metrics metrics = Metrics.create();
    private PacketRegistry packetRegistry = new PacketRegistry();
    private PacketExecutor packetExecutor = ThreadInPacketExecutor.INSTANCE;
    private NodeEventBus eventBus = new NodeEventBus();
    private CallbackProvider callbackProvider = new CallbackProvider();

    public ServerNode create() {
        return new ServerNodeImpl(
                remoteEventLoopGroup == null
                        ? new SimpleEventLoopGroup(remoteEventLoopGroupSize, remoteEventLoopFactory)
                        : remoteEventLoopGroup,
                eventLoop == null ? eventLoopFactory.create() : eventLoop,
                configuration, metrics,
                packetRegistry, packetExecutor,
                eventBus, callbackProvider
        );
    }

    public static ServerBuilder builder() {
        return new ServerBuilder();
    }

}
