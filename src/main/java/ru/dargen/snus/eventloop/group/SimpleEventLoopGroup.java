package ru.dargen.snus.eventloop.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import ru.dargen.snus.eventloop.EventLoop;
import ru.dargen.snus.eventloop.exception.EventLoopException;
import ru.dargen.snus.eventloop.factory.EventLoopFactory;
import ru.dargen.snus.util.Collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.allOf;

@Getter
@RequiredArgsConstructor
@Accessors(chain = true, fluent = true)
public class SimpleEventLoopGroup implements EventLoopGroup {

    protected final int size;
    protected final EventLoopFactory factory;
    protected final Set<EventLoop> loops = Collections.newConcurrentSet();

    protected boolean isShutdown;

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public EventLoop take() {
        if (isShutdown) {
            throw new EventLoopException("Event loop is shutdown");
        }

        if (loops.size() < size) {
            var loop = factory.create();
            loops.add(loop);
            return loop;
        }

        return loops.stream().min(Comparator.comparingInt(EventLoop::size)).get();
    }

    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    public void shutdown() {
        isShutdown = true;
        loops.forEach(EventLoop::shutdown);
    }

    @Override
    public Collection<Runnable> shutdownNow() {
        isShutdown = true;
        return loops.stream().flatMap(loop -> loop.shutdownNow().stream()).toList();
    }

    @Override
    public CompletableFuture<Void> shutdownGracefully() {
        isShutdown = true;
        return allOf(loops.stream()
                .map(EventLoop::shutdownGracefully)
                .toArray(CompletableFuture[]::new));
    }

}
