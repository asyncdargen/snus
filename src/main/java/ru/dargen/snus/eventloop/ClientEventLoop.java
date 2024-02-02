package ru.dargen.snus.eventloop;

import ru.dargen.snus.node.Node;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public class ClientEventLoop extends RemoteEventLoop {

    public ClientEventLoop(int selectTimeout) {
        super(OPTIONS | SelectionKey.OP_CONNECT, selectTimeout);
    }

    public ClientEventLoop() {
        super(OPTIONS | SelectionKey.OP_CONNECT);
    }

    @Override
    protected void inLoop() {
        super.inLoop();
        nodes.forEach(node -> node.callbackProvider().validate());
    }

}
