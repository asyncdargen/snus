package ru.dargen.snus.buffer;

import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

public class BufferPool {

    protected int poolSize, bufferSize;
    protected final Queue<BufferHolder> pool;

    public BufferPool(int poolSize, int bufferSize) {
        pool = new ArrayDeque<>(poolSize);

        this.poolSize = poolSize;
        this.bufferSize = bufferSize;
    }

    public int loadedSize() {
        return pool.size();
    }

    private BufferHolder create() {
        return new BufferHolder(ByteBuffer.allocateDirect(bufferSize));
    }

    public BufferHolder acquire() {
        BufferHolder holder;

        while ((holder = pool.poll()) != null && !holder.hasBuffer()) ;

        return holder != null ? holder : create();
    }

    public boolean release(BufferHolder holder) {
        holder.clear();

        if (holder.hasBuffer() && pool.size() < poolSize) {
            return pool.offer(holder);
        }

        return false;
    }

    public class BufferHolder implements AutoCloseable {

        private final SoftReference<ByteBuffer> reference;

        public BufferHolder(ByteBuffer buffer) {
            this.reference = new SoftReference<>(buffer);
        }


        public ByteBuffer byteBuffer() {
            return reference.get();
        }

        public Buffer buffer() {
            return Buffer.create(byteBuffer());
        }

        public boolean hasBuffer() {
            return byteBuffer() != null;
        }


        public void free() {
            clear();
            pool.remove(this);
        }

        public void clear() {
            if (hasBuffer()) {
                byteBuffer().clear();
            }
        }


        @Override
        public void close() {
            release(this);
        }

    }

}
