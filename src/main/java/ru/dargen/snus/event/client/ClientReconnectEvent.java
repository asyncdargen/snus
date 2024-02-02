package ru.dargen.snus.event.client;

import lombok.Getter;
import ru.dargen.snus.event.NodeEvent;
import ru.dargen.snus.node.RemoteNode;

@Getter
public class ClientReconnectEvent extends NodeEvent {

    private final boolean autoReconnect;

    public ClientReconnectEvent(RemoteNode remote, boolean autoReconnect) {
        super(remote);

        this.autoReconnect = autoReconnect;
    }

}
