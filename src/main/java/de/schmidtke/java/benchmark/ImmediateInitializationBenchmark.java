package de.schmidtke.java.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

public class ImmediateInitializationBenchmark {

    private static class Obj {
        public long l;

        public Obj(long l) {
            this.l = l;
        }
    }

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

    private static class ImmediateTask extends ForkJoinTask<Void> {

        private static final DevNull devNull = new DevNull();

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
            devNull.write(o.l);
            return true;
        }

    }

    private static class LazyTask extends ForkJoinTask<Void> {

        private static final DevNull devNull = new DevNull();

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
            devNull.write(o.l);
            return true;
        }

    }

    @State(value = Scope.Benchmark)
    public static class Pool {
        private final ForkJoinPool threadPool;

        public Pool() {
            threadPool = new ForkJoinPool(Runtime.getRuntime()
                    .availableProcessors(),
                    ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
        }

        public void execute(ForkJoinTask<?> t) {
            threadPool.execute(t);
        }
    }

    @Benchmark
    public void testImmediateInitialization(Pool pool) {
        pool.execute(new ImmediateTask(System.currentTimeMillis()));
    }

    @Benchmark
    public void testLazyInitialization(Pool pool) {
        pool.execute(new LazyTask(System.currentTimeMillis()));
    }

}
