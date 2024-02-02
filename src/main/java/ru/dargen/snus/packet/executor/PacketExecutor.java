package ru.dargen.snus.packet.executor;

import ru.dargen.snus.node.RemoteNode;
import ru.dargen.snus.packet.Packet;

@FunctionalInterface
public interface PacketExecutor {

    void execute(RemoteNode node, Packet packet);

}
