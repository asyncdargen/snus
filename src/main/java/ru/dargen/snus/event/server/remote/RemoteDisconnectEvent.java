package ru.dargen.snus.event.server.remote;

import lombok.Getter;
import ru.dargen.snus.event.NodeEvent;
import ru.dargen.snus.node.RemoteNode;
import ru.dargen.snus.node.ServerNode;

@Getter
public class RemoteDisconnectEvent extends NodeEvent {

    private final ServerNode server;

    public RemoteDisconnectEvent(RemoteNode node, ServerNode server) {
        super(node);
        this.server = server;
    }

    public RemoteNode getRemote() {
        return (RemoteNode) getNode();
    }

}
