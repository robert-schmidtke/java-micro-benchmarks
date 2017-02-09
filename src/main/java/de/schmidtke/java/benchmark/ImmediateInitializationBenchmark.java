package de.schmidtke.java.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.openjdk.jmh.annotations.Benchmark;

public class ImmediateInitializationBenchmark {

    private class Obj {
        public long l;

        public Obj(long l) {
            this.l = l;
        }
    }

    private class ImmediateTask extends ForkJoinTask<Void> {

        private static final long serialVersionUID = 942733636257173231L;

        private Obj o;

        public ImmediateTask(long l) {
            o = new Obj(l);
        }

        @Override
        public Void getRawResult() {
            return null;
        }

        @Override
        protected void setRawResult(Void value) {

        }

        @Override
        protected boolean exec() {
            devNull.println(o.l);
            return true;
        }

    }

    private class LazyTask extends ForkJoinTask<Void> {

        private static final long serialVersionUID = -7128207595532482970L;

        private long l;

        public LazyTask(long l) {
            this.l = l;
        }

        @Override
        public Void getRawResult() {
            return null;
        }

        @Override
        protected void setRawResult(Void value) {

        }

        @Override
        protected boolean exec() {
            Obj o = new Obj(l);
            devNull.println(o.l);
            return true;
        }

    }

    private final PrintWriter devNull;

    private final ForkJoinPool threadPool;

    public ImmediateInitializationBenchmark() {
        try {
            devNull = new PrintWriter(new File("/dev/null"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Not compatible with Windows.");
        }

        threadPool = new ForkJoinPool(Runtime.getRuntime()
                .availableProcessors(),
                ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
    }

    @Benchmark
    public void testImmediateInitialization() {
        threadPool.execute(new ImmediateTask(System.currentTimeMillis()));
    }

    @Benchmark
    public void testLazyInitialization() {
        threadPool.execute(new LazyTask(System.currentTimeMillis()));
    }

}
