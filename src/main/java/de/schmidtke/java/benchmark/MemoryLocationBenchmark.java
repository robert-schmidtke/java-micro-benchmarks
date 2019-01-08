package de.schmidtke.java.benchmark;

import java.nio.ByteBuffer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

public class MemoryLocationBenchmark {

    @State(Scope.Thread)
    public static class Wrap {
        ByteBuffer mem = ByteBuffer.wrap(new byte[8]);
        long l = System.currentTimeMillis();
    }

    @State(Scope.Thread)
    public static class Heap {
        ByteBuffer mem = ByteBuffer.allocate(8);
        long l = System.currentTimeMillis();
    }

    @State(Scope.Thread)
    public static class OffHeap {
        ByteBuffer mem = ByteBuffer.allocateDirect(8);
        long l = System.currentTimeMillis();
    }

    @State(Scope.Thread)
    public static class Object {
        private long v;
        long l = System.currentTimeMillis();

        public void setLong(long v) {
            this.v = v;
        }

        public long getLong() {
            return v;
        }
    }

    @Benchmark
    public long benchmarkWrap(Wrap w) {
        w.mem.putLong(0, w.l++);
        return w.mem.getLong(0);
    }

    @Benchmark
    public long benchmarkHeap(Heap h) {
        h.mem.putLong(0, h.l++);
        return h.mem.getLong(0);
    }

    @Benchmark
    public long benchmarkOffHeap(OffHeap oh) {
        oh.mem.putLong(0, oh.l++);
        return oh.mem.getLong(0);
    }

    @Benchmark
    public long benchmarkObject(Object o) {
        o.setLong(o.l++);
        return o.getLong();
    }

}
