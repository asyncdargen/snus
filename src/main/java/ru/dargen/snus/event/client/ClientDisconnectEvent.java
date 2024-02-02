package ru.dargen.snus.event.client;

import ru.dargen.snus.event.NodeEvent;
import ru.dargen.snus.node.ClientNode;
import ru.dargen.snus.node.RemoteNode;

public class ClientDisconnectEvent extends NodeEvent {

    public ClientDisconnectEvent(RemoteNode node) {
        super(node);
    }

    public ClientNode getClient() {
        return (ClientNode) getNode();
    }

}
