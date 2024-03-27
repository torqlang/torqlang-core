/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class BenchLexerState {

    public final String factorial = """
            local fact in
                fact = func (x) in
                    // Continuation Passing Style
                    func fact_cps(n, k) in
                        if n==0 then k
                        else fact_cps(n - 1, n * k) end
                    end
                    /*func fact_cps(n, k) in
                        if n==0 then k
                        else fact_cps(n - 1, n * k) end
                    end*/
                    fact_cps(x, 1)
                end
                fact(100m)
            end""";

}
