package ru.dargen.snus.eventloop.factory;

import lombok.RequiredArgsConstructor;
import ru.dargen.snus.eventloop.EventLoop;

import java.util.concurrent.ThreadFactory;

@RequiredArgsConstructor
public abstract class ThreadEventLoopFactory implements EventLoopFactory {

    private final ThreadFactory threadFactory;

    public ThreadEventLoopFactory(String threadName) {
        this(NamedThreadFactory.create(threadName, true));
    }

    protected abstract EventLoop createNew();

    @Override
    public EventLoop create() {
        var eventLoop = createNew();
        var thread = threadFactory.newThread(eventLoop);
        thread.start();
        return eventLoop;
    }

}
