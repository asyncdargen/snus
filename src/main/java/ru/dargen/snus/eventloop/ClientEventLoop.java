package ru.dargen.snus.eventloop;

public class ClientEventLoop extends RemoteEventLoop {

    @Override
    protected void inLoop() {
        super.inLoop();
        nodes.forEach(node -> node.callbackProvider().validate());
    }

}
