package ru.dargen.snus.util;

import lombok.experimental.UtilityClass;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@UtilityClass
public class Collections {

    public <T> Set<T> newConcurrentSet() {
        return java.util.Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    public <T> void drain(Queue<T> queue, Consumer<T> block) {
        while (!queue.isEmpty()) {
            var value = queue.poll();
            if (value != null) block.accept(value);
        }
    }

}
