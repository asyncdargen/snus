package ru.dargen.snus.packet;

import ru.dargen.snus.Snus;
import ru.dargen.snus.node.RemoteNode;
import ru.dargen.snus.util.Allocator;
import ru.dargen.snus.util.map.ComplexHashMap;
import ru.dargen.snus.util.map.ComplexMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.Level;

@SuppressWarnings("unchecked")
public class PacketRegistry {

    protected int lastPacketId;

    protected final ComplexMap<Integer, Class<Packet>> packets = new ComplexHashMap<>();
    protected final Map<Integer, Supplier<Packet>> suppliers = new ConcurrentHashMap<>();
    protected final Map<Integer, Set<PacketHandler<?>>> handlers = new ConcurrentHashMap<>();

    public int getPacketId(Class<?> packetClass) {
        var id = packets.getKey((Class<Packet>) packetClass);
        return id == null ? -1 : id;
    }

    public int getPacketId(Packet packet) {
        return getPacketId(packet.getClass());
    }

    public Class<Packet> getPacketClass(int id) {
        return packets.getValue(id);
    }

    public PacketRegistry register(int id, Class<?> packetClass, Supplier<Packet> supplier) {
        if (supplier == null) {
            supplier = (Supplier<Packet>) Allocator.allocator(packetClass);
        }

        if (id == -1) {
            id = packets.keys().stream().max(Comparator.comparingInt(id0 -> id0)).orElse(0);
        }
        lastPacketId = id;

        packets.put(id, (Class<Packet>) packetClass);
        suppliers.put(id, supplier);

        return this;
    }

    public PacketRegistry register(int id, Class<?> packetClass) {
        return register(id, packetClass, null);
    }

    public PacketRegistry register(Class<?> packetClass, Supplier<Packet> supplier) {
        var id = packetClass.isAnnotationPresent(PacketId.class)
                ? packetClass.getDeclaredAnnotation(PacketId.class).value()
                : -1;

        return register(id, packetClass, supplier);
    }

    public PacketRegistry register(Class<?> packetClass) {
        return register(packetClass, null);
    }

    public <P extends Packet> Set<PacketHandler<P>> getPacketHandlers(int packetId) {
        return ((Set<PacketHandler<P>>) (Object) handlers.getOrDefault(packetId, Collections.emptySet()));
    }

    public <P extends Packet> Set<PacketHandler<P>> getPacketHandlers(Class<P> packetClass) {
        return getPacketHandlers(getPacketId(packetClass));
    }

    public <P extends Packet> Set<PacketHandler<P>> getPacketHandlers(P packet) {
        return getPacketHandlers(getPacketId(packet));
    }

    public boolean hasPacketHandlers(int packetId) {
        return !getPacketHandlers(packetId).isEmpty();
    }

    public boolean hasPacketHandlers(Class<? extends Packet> packetClass) {
        return hasPacketHandlers(getPacketId(packetClass));
    }

    public void firePacketHandlers(Packet packet, RemoteNode node) {
        getPacketHandlers(packet).forEach(handler -> {
            try {
                handler.handle(node, packet);
            } catch (Throwable t) {
                Snus.LOGGER.log(Level.SEVERE, "Error while packet handling");
            }
        });
    }

    public <P extends Packet> PacketRegistry registerHandler(int id, PacketHandler<P> handler) {
        handlers.computeIfAbsent(id, key -> ru.dargen.snus.util.Collections.newConcurrentSet()).add(handler);

        return this;
    }

    public <P extends Packet> PacketRegistry registerHandler(Class<P> packetClass, PacketHandler<P> handler) {
        return registerHandler(getPacketId(packetClass), handler);
    }

    public <P extends Packet> PacketRegistry registerHandler(PacketHandler<P> handler) {
        return registerHandler(lastPacketId, handler);
    }

    public Packet constructPacket(int id) {
        return suppliers.containsKey(id) ? suppliers.get(id).get() : null;
    }

    public Packet constructPacket(Class<?> packetClass) {
        return constructPacket(getPacketId(packetClass));
    }

}
