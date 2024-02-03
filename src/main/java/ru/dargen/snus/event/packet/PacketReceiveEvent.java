package ru.dargen.snus.event.packet;

import lombok.Getter;
import lombok.Setter;
import ru.dargen.snus.event.NodeEvent;
import ru.dargen.snus.node.Node;
import ru.dargen.snus.node.RemoteNode;
import ru.dargen.snus.packet.Packet;

@Setter
@Getter
public class PacketReceiveEvent extends NodeEvent {

    protected Packet packet;

    public PacketReceiveEvent(Node<?> node, Packet packet) {
        super(node);
        this.packet = packet;
    }

    public RemoteNode getRemote() {
        return (RemoteNode) getNode();
    }

}
