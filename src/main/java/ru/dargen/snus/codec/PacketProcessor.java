package ru.dargen.snus.codec;

import lombok.experimental.UtilityClass;
import ru.dargen.snus.buffer.Buffer;
import ru.dargen.snus.codec.exception.ProcessPacketException;
import ru.dargen.snus.event.packet.PacketReceiveEvent;
import ru.dargen.snus.event.packet.PacketSendEvent;
import ru.dargen.snus.node.RemoteNode;
import ru.dargen.snus.packet.Packet;

import java.util.Objects;

@UtilityClass
public class PacketProcessor {

    public void processInPacket(RemoteNode node, Buffer frame) {
        var packetTypeId = frame.readInt();
        var packetUniqueId = frame.readUUID();

        var registry = node.packetRegistry();
        var packetClass = registry.getPacketClass(packetTypeId);

        if (packetClass == null) {
            throw new ProcessPacketException("Unknown packet %s".formatted(packetTypeId));
        }

        Packet packet = null;
        try {
            packet = registry.constructPacket(packetClass);
            Objects.requireNonNull(packet, "Constructed packet is null");
            packet.setUniqueId(packetUniqueId);
        } catch (Throwable t) {
            throw new ProcessPacketException("Error while packet construct %s".formatted(packetTypeId), t);
        }

        try {
            packet.read(frame);
        } catch (Throwable t) {
            throw new ProcessPacketException("Error while packet read %s".formatted(packetTypeId), t);
        }

        if (node.events().hasHandlers(PacketReceiveEvent.class)) {
            var event = node.events().fire(new PacketReceiveEvent(node, packet));
            if (event.isCancelled()) {
                return;
            }

            packet = event.getPacket();
        }

        if (packet == null) {
            throw new ProcessPacketException("Packet null");
        }

        if (node.callbackProvider().completeCallback(packet)) {
            return;
        }

        if (!registry.hasPacketHandlers(packetTypeId)) {
             throw new ProcessPacketException("Not find packet handler %s".formatted(packetTypeId));
        } else registry.firePacketHandlers(packet, node);
    }

    public void processOutPacket(RemoteNode node, Packet packet, Buffer out) {
        if (node.events().hasHandlers(PacketSendEvent.class)) {
            var event = node.events().fire(new PacketSendEvent(node, packet));
            if (event.isCancelled()) {
                return;
            }

            packet = event.getPacket();
        }

        if (packet == null) {
            throw new ProcessPacketException("Packet null");
        }

        var packetTypeId = node.packetRegistry().getPacketId(packet);

        if (packetTypeId == -1) {
            throw new ProcessPacketException("Unknown packet %s".formatted(packetTypeId));
        }

        out.writeInt(packetTypeId);
        out.writeUUID(packet.getUniqueId());
        packet.write(out);
    }

}
