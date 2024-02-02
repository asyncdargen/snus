package ru.dargen.snus.packet.executor;

import ru.dargen.snus.node.RemoteNode;
import ru.dargen.snus.packet.Packet;

public class ThreadInPacketExecutor implements PacketExecutor{

    public static final PacketExecutor INSTANCE = new ThreadInPacketExecutor();

    @Override
    public void execute(RemoteNode node, Packet packet) {
        node.packetRegistry().firePacketHandlers(packet, node);
    }

}
