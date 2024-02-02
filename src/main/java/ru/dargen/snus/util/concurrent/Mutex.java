package ru.dargen.snus.util.concurrent;

import lombok.SneakyThrows;

public class Mutex {

    public synchronized void lock() throws InterruptedException {
        wait();
    }

    public synchronized void lock(long timeout) throws InterruptedException {
        wait(timeout);
    }

    @SneakyThrows
    public synchronized void release() {
        notifyAll();
    }

}
