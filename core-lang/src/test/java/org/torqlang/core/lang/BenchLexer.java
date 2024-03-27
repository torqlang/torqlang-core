/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

/*
 * --- 2023 October ---
 *
 * Processor: Intel Core i7-10710U CPU @ 1.10GHz x 12
 * Memory:    8.0 GiB
 *
 * Java 17 Using ZGC
 *
 * Result "org.torqlang.core.lang.BenchLexer.test":
 *   436206.476 ±(99.9%) 3956.398 ops/s [Average]
 *   (min, avg, max) = (429454.816, 436206.476, 448838.344), stdev = 5281.679
 *   CI (99.9%): [432250.078, 440162.874] (assumes normal distribution)
 *
 * Benchmark         Mode  Cnt       Score      Error  Units
 * BenchLexer.test  thrpt   25  436206.476 ± 3956.398  ops/s
 *
 * Result "org.torqlang.core.lang.BenchLexer.test":
 *   442092.319 ±(99.9%) 6386.794 ops/s [Average]
 *   (min, avg, max) = (431828.354, 442092.319, 469950.862), stdev = 8526.187
 *   CI (99.9%): [435705.526, 448479.113] (assumes normal distribution)
 *
 * Benchmark         Mode  Cnt       Score      Error  Units
 * BenchLexer.test  thrpt   25  442092.319 ± 6386.794  ops/s
 *
 * // After keeping IDE open all night, I can only guess why it's faster
 *
 * Result "org.torqlang.core.lang.BenchLexer.test":
 *   467783.167 ±(99.9%) 6339.722 ops/s [Average]
 *   (min, avg, max) = (454756.241, 467783.167, 483469.960), stdev = 8463.348
 *   CI (99.9%): [461443.445, 474122.889] (assumes normal distribution)
 *
 * Benchmark         Mode  Cnt       Score      Error  Units
 * BenchLexer.test  thrpt   25  467783.167 ± 6339.722  ops/s
 *
 * Result "org.torqlang.core.lang.BenchLexer.test":
 *   471249.045 ±(99.9%) 12100.576 ops/s [Average]
 *   (min, avg, max) = (436678.536, 471249.045, 502361.682), stdev = 16153.923
 *   CI (99.9%): [459148.469, 483349.621] (assumes normal distribution)
 *
 * Benchmark         Mode  Cnt       Score       Error  Units
 * BenchLexer.test  thrpt   25  471249.045 ± 12100.576  ops/s
 */
public class BenchLexer {

    //@Benchmark
    public void test(BenchLexerState state, Blackhole blackhole) {
        Lexer lexer = new Lexer(state.factorial);
        LexerToken token = lexer.nextToken(false);
        blackhole.consume(token); // local
        token = lexer.nextToken(false);
        blackhole.consume(token); // fact
        token = lexer.nextToken(false);
        blackhole.consume(token); // in
        token = lexer.nextToken(false);
        blackhole.consume(token); // fact
        token = lexer.nextToken(false);
        blackhole.consume(token); // =
        token = lexer.nextToken(false);
        blackhole.consume(token); // func
        token = lexer.nextToken(false);
        blackhole.consume(token); // (
        token = lexer.nextToken(false);
        blackhole.consume(token); // x
        token = lexer.nextToken(false);
        blackhole.consume(token); // )
        token = lexer.nextToken(false);
        blackhole.consume(token); // in
        token = lexer.nextToken(false);
        blackhole.consume(token); // "// Continuation Passing Style"
        token = lexer.nextToken(false);
        blackhole.consume(token); // func
        token = lexer.nextToken(false);
        blackhole.consume(token); // fact_cps
        token = lexer.nextToken(false);
        blackhole.consume(token); // (
        token = lexer.nextToken(false);
        blackhole.consume(token); // n
        token = lexer.nextToken(false);
        blackhole.consume(token); // ,
        token = lexer.nextToken(false);
        blackhole.consume(token); // k
        token = lexer.nextToken(false);
        blackhole.consume(token); // )
        token = lexer.nextToken(false);
        blackhole.consume(token); // in
        token = lexer.nextToken(false);
        blackhole.consume(token); // if
        token = lexer.nextToken(false);
        blackhole.consume(token); // n
        token = lexer.nextToken(false);
        blackhole.consume(token); // ==
        token = lexer.nextToken(false);
        blackhole.consume(token); // 0
        token = lexer.nextToken(false);
        blackhole.consume(token); // then
        token = lexer.nextToken(false);
        blackhole.consume(token); // k
        token = lexer.nextToken(false);
        blackhole.consume(token); // else
        token = lexer.nextToken(false);
        blackhole.consume(token); // fact_cps
        token = lexer.nextToken(false);
        blackhole.consume(token); // (
        token = lexer.nextToken(false);
        blackhole.consume(token); // n
        token = lexer.nextToken(false);
        blackhole.consume(token); // -
        token = lexer.nextToken(false);
        blackhole.consume(token); // 1
        token = lexer.nextToken(false);
        blackhole.consume(token); // ,
        token = lexer.nextToken(false);
        blackhole.consume(token); // n
        token = lexer.nextToken(false);
        blackhole.consume(token); // *
        token = lexer.nextToken(false);
        blackhole.consume(token); // k
        token = lexer.nextToken(false);
        blackhole.consume(token); // )
        token = lexer.nextToken(false);
        blackhole.consume(token); // end
        token = lexer.nextToken(false);
        blackhole.consume(token); // end
        token = lexer.nextToken(false);
        blackhole.consume(token); // <block-comment>
        token = lexer.nextToken(false);
        blackhole.consume(token); // fact_cps
        token = lexer.nextToken(false);
        blackhole.consume(token); // (
        token = lexer.nextToken(false);
        blackhole.consume(token); // x
        token = lexer.nextToken(false);
        blackhole.consume(token); // ,
        token = lexer.nextToken(false);
        blackhole.consume(token); // 1
        token = lexer.nextToken(false);
        blackhole.consume(token); // )
        token = lexer.nextToken(false);
        blackhole.consume(token); // end
        token = lexer.nextToken(false);
        blackhole.consume(token); // fact
        token = lexer.nextToken(false);
        blackhole.consume(token); // (
        token = lexer.nextToken(false);
        blackhole.consume(token); // 100m
        token = lexer.nextToken(false);
        blackhole.consume(token); // )
        token = lexer.nextToken(false);
        blackhole.consume(token); // end
        token = lexer.nextToken(false);
        blackhole.consume(token); // EOF
        token = lexer.nextToken(false);
        blackhole.consume(token); // EOF (once EOF, you keep getting EOF)
    }

}
