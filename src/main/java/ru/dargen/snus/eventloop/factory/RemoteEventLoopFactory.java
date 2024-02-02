package ru.dargen.snus.eventloop.factory;

import ru.dargen.snus.eventloop.EventLoop;
import ru.dargen.snus.eventloop.RemoteEventLoop;
import ru.dargen.snus.util.concurrent.NamedThreadFactory;

import java.util.concurrent.ThreadFactory;

public class RemoteEventLoopFactory extends ThreadEventLoopFactory {

    public static EventLoopFactory INSTANCE = new RemoteEventLoopFactory(
            NamedThreadFactory.create("Remote-Event-Loop-Thread-%s", true)
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
