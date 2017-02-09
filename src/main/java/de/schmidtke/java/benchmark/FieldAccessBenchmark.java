package de.schmidtke.java.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

public class FieldAccessBenchmark {

    private static class Obj {
        public long l;

        public void setL(long l) {
            this.l = l;
        }

        public long getL() {
            return l;
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
    public void testFieldAccess(DevNull devNull) {
        Obj o = new Obj();
        o.l = System.currentTimeMillis();
        devNull.write(o.l);
    }

    @Benchmark
    public void testGetSet(DevNull devNull) {
        Obj o = new Obj();
        o.setL(System.currentTimeMillis());
        devNull.write(o.getL());
    }

}
