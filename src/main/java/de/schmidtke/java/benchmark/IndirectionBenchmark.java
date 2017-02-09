/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.schmidtke.java.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.openjdk.jmh.annotations.Benchmark;

public class IndirectionBenchmark {

    private static class Direct {
        private final long i;

        public Direct(long i) {
            this.i = i;
        }

        public long getI() {
            return i;
        }
    }

    private static class Indirect1 {
        private final Direct d;

        public Indirect1(long i) {
            this.d = new Direct(i);
        }

        public long getI() {
            return d.getI();
        }
    }

    private static class Indirect2 {
        private final Indirect1 i;

        public Indirect2(long i) {
            this.i = new Indirect1(i);
        }

        public long getI() {
            return i.getI();
        }
    }

    private static class Indirect3 {
        private final Indirect2 i;

        public Indirect3(long i) {
            this.i = new Indirect2(i);
        }

        public long getI() {
            return i.getI();
        }
    }

    private final PrintWriter devNull;

    public IndirectionBenchmark() {
        try {
            devNull = new PrintWriter(new File("/dev/null"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Not compatible with Windows.");
        }
    }

    @Benchmark
    public void testNoIndirection() {
        devNull.println(new Direct(System.currentTimeMillis()).getI());
    }

    @Benchmark
    public void testOneIndirection() {
        devNull.println(new Indirect1(System.currentTimeMillis()).getI());
    }

    @Benchmark
    public void testTwoIndirection() {
        devNull.println(new Indirect2(System.currentTimeMillis()).getI());
    }

    @Benchmark
    public void testThreeIndirection() {
        devNull.println(new Indirect3(System.currentTimeMillis()).getI());
    }
}
