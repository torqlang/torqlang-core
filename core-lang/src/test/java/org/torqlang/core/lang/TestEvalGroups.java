/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.Bool;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Int32;

import static org.junit.Assert.assertEquals;

public class TestEvalGroups {

    @Test
    public void test01() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = (1 + 2) * 3")
            .perform();
        assertEquals("x = (1 + 2) * 3", e.sntcOrExpr().toString());
        String expected = """
            local $v0 in
                $add(1, 2, $v0)
                $mult($v0, 3, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(9), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void test02() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = 2 * (3 + 4) * 5")
            .perform();
        assertEquals("x = 2 * (3 + 4) * 5", e.sntcOrExpr().toString());
        String expected = """
            local $v0 in
                local $v1 in
                    $add(3, 4, $v1)
                    $mult(2, $v1, $v0)
                end
                $mult($v0, 5, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(70), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void test03() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = 2 * (3 + -4) * 5")
            .perform();
        assertEquals("x = 2 * (3 + -4) * 5", e.sntcOrExpr().toString());
        String expected = """
            local $v0 in
                local $v1 in
                    local $v2 in
                        $negate(4, $v2)
                        $add(3, $v2, $v1)
                    end
                    $mult(2, $v1, $v0)
                end
                $mult($v0, 5, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(-10), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void test04() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = -9 > 2 * (3 + -4) * 5")
            .perform();
        assertEquals("x = -9 > 2 * (3 + -4) * 5", e.sntcOrExpr().toString());
        String expected = """
            local $v0, $v1 in
                $negate(9, $v0)
                local $v2 in
                    local $v3 in
                        local $v4 in
                            $negate(4, $v4)
                            $add(3, $v4, $v3)
                        end
                        $mult(2, $v3, $v2)
                    end
                    $mult($v2, 5, $v1)
                end
                $gt($v0, $v1, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Bool.TRUE, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void test05() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = -11 > 2 * (3 + -4) * 5")
            .perform();
        assertEquals("x = -11 > 2 * (3 + -4) * 5", e.sntcOrExpr().toString());
        String expected = """
            local $v0, $v1 in
                $negate(11, $v0)
                local $v2 in
                    local $v3 in
                        local $v4 in
                            $negate(4, $v4)
                            $add(3, $v4, $v3)
                        end
                        $mult(2, $v3, $v2)
                    end
                    $mult($v2, 5, $v1)
                end
                $gt($v0, $v1, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Bool.FALSE, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void test06() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = 2 * (--3 + ---4) * 5")
            .perform();
        assertEquals("x = 2 * (--3 + ---4) * 5", e.sntcOrExpr().toString());
        String expected = """
            local $v0 in
                local $v1 in
                    local $v2, $v4 in
                        local $v3 in
                            $negate(3, $v3)
                            $negate($v3, $v2)
                        end
                        local $v5 in
                            local $v6 in
                                $negate(4, $v6)
                                $negate($v6, $v5)
                            end
                            $negate($v5, $v4)
                        end
                        $add($v2, $v4, $v1)
                    end
                    $mult(2, $v1, $v0)
                end
                $mult($v0, 5, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(-10), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void test07() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = -11 > 2 * (--3 + ---4) * 5")
            .perform();
        assertEquals("x = -11 > 2 * (--3 + ---4) * 5", e.sntcOrExpr().toString());
        String expected = """
            local $v0, $v1 in
                $negate(11, $v0)
                local $v2 in
                    local $v3 in
                        local $v4, $v6 in
                            local $v5 in
                                $negate(3, $v5)
                                $negate($v5, $v4)
                            end
                            local $v7 in
                                local $v8 in
                                    $negate(4, $v8)
                                    $negate($v8, $v7)
                                end
                                $negate($v7, $v6)
                            end
                            $add($v4, $v6, $v3)
                        end
                        $mult(2, $v3, $v2)
                    end
                    $mult($v2, 5, $v1)
                end
                $gt($v0, $v1, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Bool.FALSE, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void test08_1() throws Exception {
        String source = """
            begin
                var a = 5
                x = a + 3
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local a = 5 in
                $add(a, 3, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(8), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void test08_2() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("(var a = 5; x = a + 3)")
            .perform();
        String expected = """
            (var a = 5
            x = a + 3)""";
        assertEquals(expected, e.sntcOrExpr().toString());
        expected = """
            local a = 5 in
                $add(a, 3, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(8), e.varAtName("x").valueOrVarSet());
    }

}
