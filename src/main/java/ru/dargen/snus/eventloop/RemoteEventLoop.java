package ru.dargen.snus.eventloop;

import ru.dargen.snus.Snus;
import ru.dargen.snus.node.Node;
import ru.dargen.snus.node.RemoteNode;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.logging.Level;

public class RemoteEventLoop extends AbstractEventLoop {

    public static final int OPTIONS = SelectionKey.OP_READ;

    public RemoteEventLoop(int options, int selectTimeout) {
        super(options, selectTimeout);
    }

    public RemoteEventLoop(int selectTimeout) {
        super(OPTIONS, selectTimeout);
    }

    public RemoteEventLoop() {
        super(OPTIONS, SELECT_TIMEOUT);
    }

    @Override
    protected void select(SelectionKey key, Node<?> node) throws IOException {
        var remote = (RemoteNode) node;

        if (key.isReadable()) {
            remote.flushIn();
        }
    }

    @Override
    protected void inLoop() {
        for (Node<?> node : nodes) {
            var remote = (RemoteNode) node;
            try {
                remote.flushOut();
            } catch (Throwable t) {
                Snus.LOGGER.log(Level.SEVERE, "Error while flush out %s".formatted(node), t);
            }
        }
    }

}
