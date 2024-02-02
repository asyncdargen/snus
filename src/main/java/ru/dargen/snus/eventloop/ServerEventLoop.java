package ru.dargen.snus.eventloop;

import ru.dargen.snus.node.Node;
import ru.dargen.snus.node.ServerNode;

import java.nio.channels.SelectionKey;

public class ServerEventLoop extends AbstractEventLoop {

    public ServerEventLoop(int selectTimeout) {
        super(SelectionKey.OP_ACCEPT, selectTimeout);
    }

    public ServerEventLoop() {
        this(SELECT_TIMEOUT);
    }

    @Override
    protected void select(SelectionKey key, Node<?> node) {
        if (!key.isAcceptable()) return;

        var server = (ServerNode) node;
        server.accept();
    }

    @Override
    protected void inLoop() {
        nodes.forEach(node -> node.callbackProvider().validate());
    }

}
