package ru.dargen.snus.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.dargen.snus.node.Node;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class NodeEvent {

    protected final Node<?> node;
    protected boolean cancelled;

    public void cancel() {
        setCancelled(true);
    }

}
