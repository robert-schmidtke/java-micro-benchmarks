package de.schmidtke.java.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

public class ObjectPoolBenchmark {

    public static class Obj {
        public long l;

        public Obj() {
        }
    }

    @State(value = Scope.Benchmark)
    public static class ObjectPool {

        Queue<Obj> q = new LinkedList<Obj>();

        public synchronized Obj getObj() {
            Obj obj = q.poll();
            return obj == null ? new Obj() : obj;
        }

        public synchronized void returnObj(Obj obj) {
            q.offer(obj);
        }
    }

    @State(value = Scope.Thread)
    public static class DevNull {
        private final PrintWriter devNull;

        public DevNull() {
            try {
                devNull = new PrintWriter(new File("/dev/null"));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Not compatible with Windows.");
            }
        }

        public void write(long l) {
            devNull.println(l);
        }
    }

    @Benchmark
    public void testNewObject(DevNull devNull) {
        Obj obj = new Obj();
        obj.l = System.currentTimeMillis();
        devNull.write(obj.l);
    }

    @Benchmark
    public void testObjectPool(DevNull devNull, ObjectPool pool) {
        Obj obj = pool.getObj();
        obj.l = System.currentTimeMillis();
        devNull.write(obj.l);
        pool.returnObj(obj);
    }

}
