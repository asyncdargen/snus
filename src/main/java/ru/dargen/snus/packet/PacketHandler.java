package ru.dargen.snus.packet;

import ru.dargen.snus.node.RemoteNode;

@FunctionalInterface
public interface PacketHandler<P extends Packet> {

    void handle(RemoteNode node, P packet);

}
