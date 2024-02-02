package ru.dargen.snus.util.concurrent;

import lombok.Getter;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class NamedThreadFactory implements ThreadFactory {

    private final String name;
    private final boolean daemon;

    private final ThreadGroup group;
    private final AtomicInteger counter = new AtomicInteger();

    public NamedThreadFactory(String name, boolean daemon, String threadGroupName) {
        this.name = name;
        this.daemon = daemon;

        this.group = threadGroupName == null ? null : new ThreadGroup(threadGroupName);
    }

    public void interrupt() {
        if (group != null) {
            group.interrupt();
        }
    }

    @Override
    public Thread newThread(Runnable r) {
        var thread = new Thread(group, r);
        thread.setName(name.formatted(counter.getAndIncrement()));
        thread.setDaemon(daemon);
        return thread;
    }

    public static ThreadFactory create(String name, boolean daemon, String threadGroupName) {
        return new NamedThreadFactory(name, daemon, threadGroupName);
    }

    public static ThreadFactory create(String name, boolean daemon) {
        return create(name, daemon, null);
    }

    public static ThreadFactory create(String name, String threadGroupName) {
        return create(name, false, threadGroupName);
    }

    public static ThreadFactory create(String name) {
        return create(name, false, null);
    }

}
