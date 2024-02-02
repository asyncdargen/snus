package ru.dargen.snus.util.concurrent;

public interface CycledRunnable extends Runnable {

    void loop();

    @Override
    default void run() {
        while (!Thread.interrupted()) {
            loop();
        }
    }

}
