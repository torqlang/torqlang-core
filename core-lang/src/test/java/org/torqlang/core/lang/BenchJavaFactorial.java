/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigDecimal;
import java.math.MathContext;

/*
 * --- 2023 October ---
 *
 * Processor: Intel Core i7-10710U CPU @ 1.10GHz x 12
 * Memory:    8.0 GiB
 *
 * Java 17 Using ZGC
 *
 * Result "org.torqlang.core.lang.BenchJavaFactorial.test":
 *   75937.821 ±(99.9%) 2553.024 ops/s [Average]
 *   (min, avg, max) = (69801.131, 75937.821, 81065.038), stdev = 3408.213
 *   CI (99.9%): [73384.798, 78490.845] (assumes normal distribution)
 *
 * Benchmark                 Mode  Cnt      Score      Error  Units
 * BenchJavaFactorial.test  thrpt   25  75937.821 ± 2553.024  ops/s
 */
public class BenchJavaFactorial {

    private static final BigDecimal FACTORIAL_100_DECIMAL128 =
        new BigDecimal("9.332621544394415268169923885626670E+157", MathContext.DECIMAL128);


    public static void main(String[] args) throws Exception {
        checkFactorial();
    }

    public static void checkFactorial() {
        BigDecimal result = new BigDecimal("1", MathContext.DECIMAL128);
        BigDecimal remaining = new BigDecimal("100", MathContext.DECIMAL128);
        while (remaining.intValue() != 0) {
            result = result.multiply(remaining, MathContext.DECIMAL128);
            remaining = remaining.subtract(BigDecimal.ONE, MathContext.DECIMAL128);
        }
        if (!result.equals(FACTORIAL_100_DECIMAL128)) {
            throw new IllegalStateException("Invalid factorial");
        }
    }

    //@Benchmark
    public void test(BenchJavaFactorialState state, Blackhole blackhole) {
        BigDecimal result = state.initial;
        BigDecimal remaining = state.request;
        while (remaining.intValue() != 0) {
            result = result.multiply(remaining, MathContext.DECIMAL128);
            blackhole.consume(result);
            remaining = remaining.subtract(BigDecimal.ONE, MathContext.DECIMAL128);
            blackhole.consume(remaining);
        }
        if (!result.equals(FACTORIAL_100_DECIMAL128)) {
            throw new IllegalStateException("Invalid factorial");
        }
    }

}
