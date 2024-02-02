package ru.dargen.snus.eventloop.factory;

import ru.dargen.snus.eventloop.ClientEventLoop;
import ru.dargen.snus.eventloop.EventLoop;

import java.util.concurrent.ThreadFactory;

public class ClientEventLoopFactory extends ThreadEventLoopFactory {

    public static EventLoopFactory INSTANCE = new ClientEventLoopFactory(
            Thread.ofVirtual().name("Client-Event-Loop-Thread-%s", 0).factory()
    );

    public ClientEventLoopFactory(ThreadFactory threadFactory) {
        super(threadFactory);
    }

    @Override
    protected EventLoop createNew() {
        return new ClientEventLoop();
    }

    public static EventLoop createEventLoop() {
        return INSTANCE.create();
    }

}
