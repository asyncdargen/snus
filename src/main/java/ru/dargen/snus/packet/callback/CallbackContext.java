package ru.dargen.snus.packet.callback;

import ru.dargen.snus.node.RemoteNode;
import ru.dargen.snus.packet.Packet;

import java.util.UUID;

public record CallbackContext<P extends Packet>(UUID uuid, P response, RemoteNode remote) {

    public void respond(Packet packet) {
        remote.respond(response, packet);
    }

    public void respondAndFlush(Packet packet) {
        remote.respondAndFlush(response, packet);
    }

    public <RP extends Packet> Callback<RP> respondCallback(Packet packet) {
        return remote.respondCallback(response, packet);
    }

    public <RP extends Packet> Callback<RP> respondCallbackAndFlush(Packet packet) {
        return remote.respondCallbackAndFlush(response, packet);
    }

}
