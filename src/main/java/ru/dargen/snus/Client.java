package ru.dargen.snus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.dargen.snus.attribute.AttributeKey;
import ru.dargen.snus.event.client.ClientConnectEvent;
import ru.dargen.snus.packet.PacketRegistry;

public class Client {

    static AttributeKey<String> ID = AttributeKey.get("id");

    public static void main(String[] args) throws Throwable {
        var packets = new PacketRegistry();

        packets.register(PacketResponse.class)
                .register(Packet.class)
                .<Packet>registerHandler((remote, packet) -> remote.respond(packet, new Packet()));

        var server = Snus.server().packetRegistry(packets).create();
        var client = Snus.client().packetRegistry(packets).create();

        client.events().register(ClientConnectEvent.class, event -> {
            event.getClient().attr(ID).set("test-" + System.currentTimeMillis());
        });

        server.bind(388);
        client.connect(388);

        client.writeCallback(new Packet())
                .timeout(1_000, () -> System.out.println("Fuck black pidors"))
                .await(ctx -> ctx.respond(new PacketResponse()));

        Thread.sleep(100000);
    }

    @Getter
    @NoArgsConstructor
    static class Packet extends ru.dargen.snus.packet.Packet {

    }

    @Getter
    @NoArgsConstructor
    static class PacketResponse extends ru.dargen.snus.packet.Packet {

    }

}
