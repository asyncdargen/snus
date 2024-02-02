package ru.dargen.snus.configuration;

import lombok.*;

import java.util.concurrent.atomic.LongAdder;

@RequiredArgsConstructor
public class Metrics {

    @Getter
    private final Metrics parent;

    public Metrics() {
        this(null);
    }

    protected long
            timestamp = System.currentTimeMillis(),
            lastPacketSentTimestamp = System.currentTimeMillis(),
            lastReceiveTimestamp = System.currentTimeMillis();

    private final LongAdder
            outPackets = new LongAdder(), inPackets = new LongAdder(),
            outBytes = new LongAdder(), inBytes = new LongAdder();

    public long getOutBytes() {
        return outBytes.longValue();
    }

    public long getInBytes() {
        return inBytes.longValue();
    }

    public long getIOBytes() {
        return getOutBytes() + getInBytes();
    }

    public long getOutPackets() {
        return outPackets.longValue();
    }

    public long getInPackets() {
        return inPackets.longValue();
    }

    public long getIOPackets() {
        return getInPackets() + getOutPackets();
    }

    public long getOutAveragePPS() {
        return getOutPackets() / (getRunningTime() / 1000);
    }

    public long getInAveragePPS() {
        return getInPackets() / (getRunningTime() / 1000);
    }

    public long getIOAveragePPS() {
        return getIOPackets() / (getRunningTime() / 1000);
    }

    public long getOutAverageBytesTraffic() {
        return getOutBytes() / (getRunningTime() / 1000);
    }

    public long getInAverageBytesTraffic() {
        return getInBytes() / (getRunningTime() / 1000);
    }

    public long getIOAverageBytesTraffic() {
        return getIOBytes() / (getRunningTime() / 1000);
    }

    public double getOutAverageTraffic(TrafficUnit unit) {
        return getOutAverageBytesTraffic() / (double) unit.getSize();
    }

    public double getInAverageTraffic(TrafficUnit unit) {
        return getInAverageBytesTraffic() / (double) unit.getSize();
    }

    public double getIOAverageTraffic(TrafficUnit unit) {
        return getIOAverageBytesTraffic() / (double) unit.getSize();
    }


    public long getRunningTime() {
        return System.currentTimeMillis() - timestamp;
    }

    public void incrementOutBytes(long bytes) {
        outBytes.add(bytes);
        if (parent != null) parent.incrementOutBytes(bytes);
    }

    public void incrementInBytes(long bytes) {
        inBytes.add(bytes);
        if (parent != null) parent.incrementInBytes(bytes);
    }

    public void incrementOutPackets() {
        outPackets.increment();
        lastReceiveTimestamp = System.currentTimeMillis();

        if (parent != null) {
            parent.incrementOutPackets();
            parent.lastReceiveTimestamp = lastReceiveTimestamp;
        }
    }

    public void incrementInPackets() {
        inPackets.increment();
        lastPacketSentTimestamp = System.currentTimeMillis();

        if (parent != null) {
            parent.incrementInPackets();
            parent.lastPacketSentTimestamp = lastPacketSentTimestamp;
        }
    }

    public void incrementOutPackets(long bytes) {
        incrementOutPackets();
        incrementOutBytes(bytes);
    }

    public void incrementInPackets(long bytes) {
        incrementInPackets();
        incrementInBytes(bytes);
    }

    public Metrics fork() {
        return new Metrics(this);
    }

    public static Metrics create() {
        return new Metrics();
    }

}
