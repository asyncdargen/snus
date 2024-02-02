package ru.dargen.snus.event.server;

import ru.dargen.snus.event.NodeEvent;
import ru.dargen.snus.node.ServerNode;

public class ServerBoundEvent extends NodeEvent {

    public ServerBoundEvent(ServerNode node) {
        super(node);
    }

    public ServerNode getServer() {
        return (ServerNode) getNode();
    }

}
