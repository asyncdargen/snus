package ru.dargen.snus.eventloop.factory;

import ru.dargen.snus.eventloop.EventLoop;
import ru.dargen.snus.eventloop.ServerEventLoop;

import java.util.concurrent.ThreadFactory;

public class ServerEventLoopFactory extends ThreadEventLoopFactory {

    public static EventLoopFactory INSTANCE = new ServerEventLoopFactory(
            Thread.ofVirtual().name("Server-Event-Loop-Thread-%s", 0).factory()
    );

    public ServerEventLoopFactory(ThreadFactory threadFactory) {
        super(threadFactory);
    }

    @Override
    protected EventLoop createNew() {
        return new ServerEventLoop();
    }

    public static EventLoop createEventLoop() {
        return INSTANCE.create();
    }

}
