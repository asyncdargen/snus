package ru.dargen.snus.eventloop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import ru.dargen.snus.Snus;
import ru.dargen.snus.eventloop.exception.EventLoopException;
import ru.dargen.snus.node.Node;
import ru.dargen.snus.util.Collections;
import ru.dargen.snus.util.NioUtil;
import ru.dargen.snus.util.concurrent.CycledRunnable;
import ru.dargen.snus.util.concurrent.Mutex;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Getter
@RequiredArgsConstructor
@ExtensionMethod({Collections.class})
@Accessors(fluent = true, chain = true)
public abstract class AbstractEventLoop implements EventLoop, CycledRunnable {

    public static int SELECT_TIMEOUT = 10;

    protected final int options;
    protected final int selectTimeout;
    protected final Selector selector = NioUtil.selector();

    protected final Set<Node<?>> nodes = Collections.newConcurrentSet();
    protected final Queue<Runnable> tasks = new ArrayDeque<>();

    protected boolean forceLoop;
    protected final Mutex mutex = new Mutex();

    protected boolean isShutdown;
    protected CompletableFuture<Void> shutdownFuture;

    protected abstract void select(SelectionKey key, Node<?> node) throws IOException;

    protected void inLoop() {
    }

    @Override
    public void forceLoop() {
        forceLoop = true;
        mutex.release();
    }

    @SneakyThrows
    protected boolean doWait() {
        return !forceLoop && tasks.isEmpty() && selector.selectNow() == 0;
    }

    @Override
    public void loop() {
        try {
            if (doWait()) {
                mutex.lock(selectTimeout);
                forceLoop = false;
            }

            try {
                synchronized (tasks) {
                    tasks.drain(Runnable::run);
                }
            } catch (Throwable t) {
                Snus.LOGGER.log(Level.SEVERE, "Error while loop tasks", t);
            }

            if (isShutdown) {
                if (shutdownFuture != null) {
                    shutdownFuture.complete(null);
                }

                Thread.currentThread().interrupt();
                selector.close();
                return;
            }

            for (Node<?> node : nodes) {
                if (!node.isAlive()) {
                    node.closeSafe();
                }
            }

            selector.selectNow(key -> {
                var node = (Node<?>) key.attachment();
                try {
                    if (!node.channel().isOpen()) {
                        throw new IOException("Node channel closed");
                    }

                    select(key, (Node<?>) key.attachment());
                } catch (IOException e) {
                    node.closeSafe();
                } catch (Throwable t) {
                    Snus.LOGGER.log(Level.SEVERE, "Error while key select %s".formatted(node), t);
                    node.closeSafe();
                }
            });

            inLoop();
        } catch (Throwable t) {
            Snus.LOGGER.log(Level.SEVERE, "Error while loop", t);
        }
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    @SneakyThrows
    public void register(Node<?> node) {
        if (isShutdown) {
            throw new EventLoopException("Event loop is shutdown");
        }

        nodes.add(node);

        node.channel().configureBlocking(false);
        node.channel().register(selector, options, node);

        selector.wakeup();
    }

    @Override
    public void unregister(Node<?> node) {
        nodes.remove(node);

        var key = node.channel().keyFor(selector);
        if (key != null) {
            key.cancel();
            selector.wakeup();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        synchronized (tasks) {
            if (!isShutdown) {
                tasks.add(runnable);
            }
        }
    }

    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    @SneakyThrows
    public void shutdown() {
        forceLoop();
        isShutdown = true;
    }

    @Override
    public Collection<Runnable> shutdownNow() {
        shutdown();
        synchronized (tasks) {
            try {
                return new ArrayList<>(tasks);
            } finally {
                tasks.clear();
            }
        }
    }

    @Override
    public CompletableFuture<Void> shutdownGracefully() {
        shutdown();
        return shutdownFuture = new CompletableFuture<>();
    }

}
