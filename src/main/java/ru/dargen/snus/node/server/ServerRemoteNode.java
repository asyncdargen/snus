package ru.dargen.snus.node.server;

import ru.dargen.snus.Snus;
import ru.dargen.snus.event.server.remote.RemoteDisconnectEvent;
import ru.dargen.snus.eventloop.EventLoop;
import ru.dargen.snus.node.ServerNode;
import ru.dargen.snus.node.base.AbstractRemoteNode;

import java.nio.channels.SocketChannel;

public class ServerRemoteNode extends AbstractRemoteNode {

    private final ServerNode serverNode;

    public ServerRemoteNode(SocketChannel channel, ServerNode serverNode, EventLoop eventLoop) {
        super(eventLoop,
                serverNode.configuration(), serverNode.metrics().fork(),
                serverNode.packetRegistry(), serverNode.packetExecutor(),
                serverNode.events(), serverNode.callbackProvider()
        );
        this.channel = channel;
        this.serverNode = serverNode;

        configuration().configure(this);
    }

    @Override
    public void close() {
        serverNode.nodes().remove(this);
        events().fire(new RemoteDisconnectEvent(this, serverNode));
        Snus.LOGGER.info("Remote disconnected %s".formatted(getSocketAddress()));
        super.close();
    }

}
