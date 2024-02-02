package ru.dargen.snus.eventloop.factory;

import ru.dargen.snus.eventloop.EventLoop;
import ru.dargen.snus.eventloop.ServerEventLoop;
import ru.dargen.snus.util.concurrent.NamedThreadFactory;

import java.util.concurrent.ThreadFactory;

public class ServerEventLoopFactory extends ThreadEventLoopFactory {

    public static EventLoopFactory INSTANCE = new ServerEventLoopFactory(
            NamedThreadFactory.create("Server-Event-Loop-Thread-%s", true)
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
