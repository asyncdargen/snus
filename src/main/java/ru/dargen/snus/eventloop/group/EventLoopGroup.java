package ru.dargen.snus.eventloop.group;

import ru.dargen.snus.eventloop.EventLoop;
import ru.dargen.snus.eventloop.factory.EventLoopFactory;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface EventLoopGroup {

    EventLoopFactory factory();

    int size();

    Set<EventLoop> loops();

    EventLoop take();

    boolean isShutdown();

    void shutdown();

    Collection<Runnable> shutdownNow();

    CompletableFuture<Void> shutdownGracefully();

}
