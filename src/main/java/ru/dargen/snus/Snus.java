package ru.dargen.snus;

import lombok.experimental.UtilityClass;
import ru.dargen.snus.builder.ClientBuilder;
import ru.dargen.snus.builder.ServerBuilder;
import ru.dargen.snus.packet.executor.PacketExecutor;
import ru.dargen.snus.packet.executor.ParallelThreadPacketExecutor;

import java.util.logging.Logger;

@UtilityClass
public class Snus {

    public static final Logger LOGGER = Logger.getLogger("Snus");

    public PacketExecutor parallelPacketExecutor(int threads) {
        return new ParallelThreadPacketExecutor(threads);
    }

    public PacketExecutor parallelPacketExecutor() {
        return parallelPacketExecutor(Runtime.getRuntime().availableProcessors());
    }

    public ClientBuilder client() {
        return ClientBuilder.builder();
    }

    public ServerBuilder server() {
        return ServerBuilder.builder();
    }

}
