package de.schmidtke.java.benchmark;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import sun.misc.Unsafe;

public class MemoryLocationBenchmark {

    @State(Scope.Thread)
    public static class WrapState {
        ByteBuffer mem = ByteBuffer.wrap(new byte[8]);
        long l = System.currentTimeMillis();
    }

    @State(Scope.Thread)
    public static class HeapState {
        ByteBuffer mem = ByteBuffer.allocate(8);
        long l = System.currentTimeMillis();
    }

    @State(Scope.Thread)
    public static class OffHeapState {
        ByteBuffer mem = ByteBuffer.allocateDirect(8);
        long l = System.currentTimeMillis();
    }

    public static class Object {
        private long l;

        public void setLong(long l) {
            this.l = l;
        }

        public long getLong() {
            return this.l;
        }
    }

    @State(Scope.Thread)
    public static class UnsafeState {
        Unsafe mem;
        long address;
        long l = System.currentTimeMillis();

        @Setup
        public void setup() {
            try {
                Field theUnsafeField = Unsafe.class
                        .getDeclaredField("theUnsafe");
                theUnsafeField.setAccessible(true);
                mem = (Unsafe) theUnsafeField.get(null);
            } catch (Exception e) {
                throw new RuntimeException("Could not get theUnsafe", e);
            }

            address = mem.allocateMemory(8);
        }

        @TearDown
        public void tearDown() {
            mem.freeMemory(address);
        }
    }

    @State(Scope.Thread)
    public static class ObjectState {
        Object mem = new Object();
        long l = System.currentTimeMillis();
    }

    @Benchmark
    public long benchmarkWrap(WrapState s) {
        s.mem.putLong(0, s.l++);
        return s.mem.getLong(0);
    }

    @Benchmark
    public long benchmarkHeap(HeapState s) {
        s.mem.putLong(0, s.l++);
        return s.mem.getLong(0);
    }

    @Benchmark
    public long benchmarkOffHeap(OffHeapState s) {
        s.mem.putLong(0, s.l++);
        return s.mem.getLong(0);
    }

    @Benchmark
    public long benchmarkObject(ObjectState s) {
        s.mem.setLong(s.l++);
        return s.mem.getLong();
    }

    @Benchmark
    public long benchmarkUnsafe(UnsafeState s) {
        s.mem.putLong(s.address, s.l++);
        return s.mem.getLong(s.address);
    }

}
