package ru.dargen.snus.packet.callback;

import lombok.Getter;
import ru.dargen.snus.node.RemoteNode;
import ru.dargen.snus.packet.Packet;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@SuppressWarnings("unchecked")
public class CallbackProvider {

    public static long DELAY = 100;

    protected long timestamp = 0;
    protected final Map<UUID, Callback<Packet>> callbackMap = new ConcurrentHashMap<>();

    public <P extends Packet> Callback<P> get(UUID id) {
        return (Callback<P>) callbackMap.get(id);
    }

    public <P extends Packet> Callback<P> remove(UUID id) {
        Callback<P> callback = (Callback<P>) callbackMap.remove(id);
        if (callback != null) {
            callback.close();
        }
        return callback;
    }

    public void validate() {
        if (callbackMap.isEmpty() || System.currentTimeMillis() < timestamp) return;

        timestamp = System.currentTimeMillis();
        callbackMap.values().removeIf(callback -> !callback.validate());
    }

    public <P extends Packet> Callback<P> create(Packet packet, RemoteNode node) {
        var callback = new Callback<P>(packet.getUniqueId(), node);
        callbackMap.put(packet.getUniqueId(), (Callback<Packet>) callback);
        return callback;
    }

    public boolean completeCallback(Packet packet) {
        Callback<Packet> callback = get(packet.getUniqueId());
        if (callback != null) {
            callbackMap.remove(packet.getUniqueId());
            try {
                return callback.complete(packet);
            } catch (Throwable e) {
                throw new CallbackException(callback.id(), "Exception while complete callback", e);
            }
        }

        return false;
    }

}
