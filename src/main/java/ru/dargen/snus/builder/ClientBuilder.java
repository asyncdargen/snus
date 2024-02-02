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
import ru.dargen.snus.eventloop.factory.ClientEventLoopFactory;
import ru.dargen.snus.eventloop.factory.EventLoopFactory;
import ru.dargen.snus.node.ClientNode;
import ru.dargen.snus.node.client.ClientRemoteNode;
import ru.dargen.snus.packet.PacketRegistry;
import ru.dargen.snus.packet.callback.CallbackProvider;
import ru.dargen.snus.packet.executor.PacketExecutor;
import ru.dargen.snus.packet.executor.ThreadInPacketExecutor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class ClientBuilder {

    private EventLoop eventLoop;
    private EventLoopFactory eventLoopFactory = ClientEventLoopFactory.INSTANCE;
    private NodeConfiguration configuration = NodeConfiguration.builder().build();
    private Metrics metrics = Metrics.create();
    private PacketRegistry packetRegistry = new PacketRegistry();
    private PacketExecutor packetExecutor = ThreadInPacketExecutor.INSTANCE;
    private NodeEventBus eventBus = new NodeEventBus();
    private CallbackProvider callbackProvider = new CallbackProvider();
    private boolean autoReconnect = true;

    public ClientNode create() {
        var client = new ClientRemoteNode(
                eventLoop == null ? eventLoopFactory.create() : eventLoop,
                configuration, metrics,
                packetRegistry, packetExecutor,
                eventBus, callbackProvider
        );
        client.setAutoReconnect(autoReconnect);
        return client;
    }

    public static ClientBuilder builder() {
        return new ClientBuilder();
    }

}
