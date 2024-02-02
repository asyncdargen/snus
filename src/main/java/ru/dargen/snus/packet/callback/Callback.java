package ru.dargen.snus.packet.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.dargen.snus.node.RemoteNode;
import ru.dargen.snus.packet.Packet;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
@Accessors(chain = true, fluent = true)
public class Callback<P extends Packet> {

    protected final UUID id;
    protected final RemoteNode remote;

    @Setter
    protected Runnable timeoutHandler;
    protected long timeoutTimestamp;

    protected final CompletableFuture<CallbackContext<P>> await;

    public Callback(UUID id, RemoteNode node) {
        this.id = id;
        this.remote = node;

        await = new CompletableFuture<>();

        timeout(10_000);
    }

    public Callback<P> timeout(long timeout) {
        this.timeoutTimestamp = System.currentTimeMillis() + timeout;
        return this;
    }

    public Callback<P> timeout(long timeout, Runnable timeoutHandler) {
        return timeout(timeout).timeoutHandler(timeoutHandler);
    }

    protected boolean complete(P packet) {
        if (!await.isDone()) {
            await.complete(new CallbackContext<>(id, packet, remote));
            return true;
        }
        return false;
    }

    protected boolean validate() {
        if (System.currentTimeMillis() > timeoutTimestamp) {
            close();

            return false;
        }

        return true;
    }

    public void close() {
        if (!await.isDone()) {
            if (timeoutHandler != null) {
                timeoutHandler.run();
            }
            await.completeExceptionally(new TimeoutException());
        }
    }

    public Callback<P> await(Consumer<CallbackContext<P>> handler) {
        await.thenAccept(handler);
        return this;
    }

    public CallbackContext<P> await(long time, TimeUnit unit) {
        var handler = await();
        try {
            return handler.get(time, unit);
        } catch (InterruptedException e) {
            throw new CallbackException(id, "Exception while wait callback", e);
        } catch (ExecutionException e) {
            throw new CallbackException(id, "Exception while get callback response", e);
        } catch (TimeoutException e) {
            throw new CallbackException(id, "Callback is timeout", e);
        }
    }

}
