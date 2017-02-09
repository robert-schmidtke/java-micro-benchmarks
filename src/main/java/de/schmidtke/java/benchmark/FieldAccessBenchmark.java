package de.schmidtke.java.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.openjdk.jmh.annotations.Benchmark;

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

    private final PrintWriter devNull;

    public FieldAccessBenchmark() {
        try {
            devNull = new PrintWriter(new File("/dev/null"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Not compatible with Windows.");
        }
    }

    @Benchmark
    public void testFieldAccess() {
        Obj o = new Obj();
        o.l = System.currentTimeMillis();
        devNull.println(o.l);
    }

    @Benchmark
    public void testGetSet() {
        Obj o = new Obj();
        o.setL(System.currentTimeMillis());
        devNull.println(o.getL());
    }

}
