/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.Dec128;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Var;

import static org.junit.Assert.assertEquals;

public class TestEvalFactorial {

    @Test
    public void test01() throws Exception {
        String source = """
            begin
                func fact(x) in
                    func fact_cps(n, k) in
                        if n < 2m then
                            k
                        else
                            fact_cps(n - 1m, n * k)
                        end
                    end
                    fact_cps(x, 1m)
                end
                x = fact(a)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(Dec128.of("10")))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local fact in
                $create_proc(proc (x, $r) in
                    local fact_cps in
                        $create_proc(proc (n, k, $r) in // free vars: fact_cps
                            local $v0 in
                                $lt(n, 2m, $v0)
                                if $v0 then
                                    $bind(k, $r)
                                else
                                    local $v1, $v2 in
                                        $sub(n, 1m, $v1)
                                        $mult(n, k, $v2)
                                        fact_cps($v1, $v2, $r)
                                    end
                                end
                            end
                        end, fact_cps)
                        fact_cps(x, 1m, $r)
                    end
                end, fact)
                fact(a, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Dec128.of("3628800"), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void test02() throws Exception {
        String source = """
            begin
                func fact(x) in
                    func fact_cps(n, k) in
                        if n < 2m then
                            k
                        else
                            fact_cps(n - 1m, n * k)
                        end
                    end
                    fact_cps(x, 1m)
                end
                x = fact(a) + fact(a + 1m)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("a"), new Var(Dec128.of("10")))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local fact in
                $create_proc(proc (x, $r) in
                    local fact_cps in
                        $create_proc(proc (n, k, $r) in // free vars: fact_cps
                            local $v0 in
                                $lt(n, 2m, $v0)
                                if $v0 then
                                    $bind(k, $r)
                                else
                                    local $v1, $v2 in
                                        $sub(n, 1m, $v1)
                                        $mult(n, k, $v2)
                                        fact_cps($v1, $v2, $r)
                                    end
                                end
                            end
                        end, fact_cps)
                        fact_cps(x, 1m, $r)
                    end
                end, fact)
                local $v3, $v4 in
                    fact(a, $v3)
                    local $v5 in
                        $add(a, 1m, $v5)
                        fact($v5, $v4)
                    end
                    $add($v3, $v4, x)
                end
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Dec128.of("43545600"), e.varAtName("x").valueOrVarSet());
    }

}
