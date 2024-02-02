package ru.dargen.snus.eventloop;

import ru.dargen.snus.node.Node;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface EventLoop extends Runnable {

    int size();

    Set<Node<?>> nodes();

    void register(Node<?> node);

    void unregister(Node<?> node);

    void execute(Runnable runnable);

    void forceLoop();

    boolean isShutdown();

    void shutdown();

    Collection<Runnable> shutdownNow();

    CompletableFuture<Void> shutdownGracefully();

}
