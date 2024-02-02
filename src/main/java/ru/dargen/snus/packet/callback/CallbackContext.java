package ru.dargen.snus.packet.callback;

import ru.dargen.snus.node.RemoteNode;

import java.util.UUID;

public record CallbackContext<P>(UUID uuid, P response, RemoteNode remote) {



}
