package ru.dargen.snus.eventloop.factory;

import ru.dargen.snus.eventloop.EventLoop;
import ru.dargen.snus.eventloop.RemoteEventLoop;

import java.util.concurrent.ThreadFactory;

public class RemoteEventLoopFactory extends ThreadEventLoopFactory {

    public static EventLoopFactory INSTANCE = new RemoteEventLoopFactory(
            Thread.ofVirtual().name("Remote-Event-Loop-Thread-%s", 0).factory()
    );

    public RemoteEventLoopFactory(ThreadFactory threadFactory) {
        super(threadFactory);
    }

    @Override
    protected EventLoop createNew() {
        return new RemoteEventLoop();
    }

    public static EventLoop createEventLoop() {
        return INSTANCE.create();
    }

}
