package ru.dargen.snus.event.server;

import ru.dargen.snus.event.NodeEvent;
import ru.dargen.snus.node.ServerNode;

public class ServerCloseEvent extends NodeEvent {

    public ServerCloseEvent(ServerNode node) {
        super(node);
    }

    public ServerNode getServer() {
        return (ServerNode) getNode();
    }

}
