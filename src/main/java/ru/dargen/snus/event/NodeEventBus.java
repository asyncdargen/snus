package ru.dargen.snus.event;

import ru.dargen.snus.Snus;
import ru.dargen.snus.util.Collections;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;

@SuppressWarnings("unchecked")
public class NodeEventBus {

    private final Map<Class<? extends NodeEvent>, Set<Consumer<NodeEvent>>> handlers = new ConcurrentHashMap<>();

    public <E extends NodeEvent> void register(Class<E> eventClass, Consumer<E> handler) {
        handlers.computeIfAbsent(eventClass, type -> Collections.newConcurrentSet())
                .add((Consumer<NodeEvent>) handler);
    }

    public void unregister(Class<? extends NodeEvent> eventClass, Consumer<?> handler) {
        var handlerSet = handlers.get(eventClass);

        if (handlerSet == null) {
            return;
        }

        handlerSet.remove(handler);

        if (handlerSet.isEmpty()) {
            handlers.remove(eventClass);
        }
    }

    public void unregisterAll(Class<? extends NodeEvent> eventClass) {
        handlers.remove(eventClass);
    }

    public <E extends NodeEvent> Set<Consumer<E>> getHandlers(Class<E> eventClass) {
        return (Set<Consumer<E>>) (Object) handlers.getOrDefault(eventClass, java.util.Collections.emptySet());
    }

    public boolean hasHandlers(Class<? extends NodeEvent> eventClass) {
        return handlers.containsKey(eventClass) && !handlers.get(eventClass).isEmpty();
    }

    public <E extends NodeEvent> E fire(E event) {
        var eventClass = (Class<E>) event.getClass();
        if (hasHandlers(eventClass)) getHandlers(eventClass).forEach(handler -> {
            try {
                handler.accept(event);
            } catch (Throwable t) {
                Snus.LOGGER.log(Level.SEVERE, "Error while event handling " + eventClass.getSimpleName(), t);
            }
        });
        return event;
    }

    public boolean fireResult(NodeEvent event) {
        return !fire(event).isCancelled();
    }

}
