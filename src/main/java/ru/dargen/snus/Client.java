package ru.dargen.snus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.dargen.snus.attribute.AttributeKey;
import ru.dargen.snus.buffer.Buffer;
import ru.dargen.snus.builder.ClientBuilder;
import ru.dargen.snus.builder.ServerBuilder;
import ru.dargen.snus.event.client.ClientConnectEvent;
import ru.dargen.snus.packet.PacketRegistry;

public class Client {

    static AttributeKey<String> ID = AttributeKey.get("id");

    public static void main(String[] args) throws Throwable {
        var packets = new PacketRegistry();

        packets.register(Packet.class).<Packet>registerHandler((remote, packet) -> {
//            remote.respond(packet, new Packet(remote.toString()));
            System.out.println(packet + " " + remote);
        });

        var server = ServerBuilder.builder().packetRegistry(packets).create();//.packetExecutor(new ExecutorServicePacketExecutor(4)).create();
        var client = ClientBuilder.builder().packetRegistry(packets).create();

        client.events().register(ClientConnectEvent.class, event -> {
            event.getClient().attr(ID).set("test-" + System.currentTimeMillis());
        });

        server.bind(388);
        client.connect(388);

        Thread.sleep(100000);
    }

    @Getter
    @NoArgsConstructor
    static class Packet extends ru.dargen.snus.packet.Packet {

        private long delta;

        @Override
        public void read(Buffer buffer) {
        }

        @Override
        public void write(Buffer buffer) {
        }
    }

}
