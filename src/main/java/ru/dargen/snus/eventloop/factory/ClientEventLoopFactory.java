package ru.dargen.snus.eventloop.factory;

import ru.dargen.snus.eventloop.ClientEventLoop;
import ru.dargen.snus.eventloop.EventLoop;
import ru.dargen.snus.util.concurrent.NamedThreadFactory;

import java.util.concurrent.ThreadFactory;

public class ClientEventLoopFactory extends ThreadEventLoopFactory {

    public static EventLoopFactory INSTANCE = new ClientEventLoopFactory(
            NamedThreadFactory.create("Client-Event-Loop-Thread-%s", true)
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
