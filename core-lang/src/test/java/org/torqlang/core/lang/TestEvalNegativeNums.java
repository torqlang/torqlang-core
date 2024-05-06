/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Int64;
import org.torqlang.core.klvm.Var;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestEvalNegativeNums {

    @Test
    public void testInt32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = 1")
            .perform();
        assertEquals("x = 1", e.sntcOrExpr().toString());
        assertEquals("$bind(1, x)", e.kernel().toString());
        assertEquals(Int32.I32_1, e.varAtName("x").valueOrVarSet());

        e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = -1")
            .perform();
        assertEquals("x = -1", e.sntcOrExpr().toString());
        assertEquals("$negate(1, x)", e.kernel().toString());
        assertEquals(Int32.of(-1), e.varAtName("x").valueOrVarSet());

        e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = --1")
            .perform();
        assertEquals("x = --1", e.sntcOrExpr().toString());
        String expected = """
            local $v0 in
                $negate(1, $v0)
                $negate($v0, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.I32_1, e.varAtName("x").valueOrVarSet());

        e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = ---1")
            .perform();
        assertEquals("x = ---1", e.sntcOrExpr().toString());
        expected = """
            local $v0 in
                local $v1 in
                    $negate(1, $v1)
                    $negate($v1, $v0)
                end
                $negate($v0, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(-1), e.varAtName("x").valueOrVarSet());

        e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_5))
            .setSource("x = a")
            .perform();
        assertEquals("x = a", e.sntcOrExpr().toString());
        assertEquals("$bind(a, x)", e.kernel().toString());
        assertEquals(Int32.of(5), e.varAtName("x").valueOrVarSet());

        e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_5))
            .setSource("x = -a")
            .perform();
        assertEquals("x = -a", e.sntcOrExpr().toString());
        assertEquals("$negate(a, x)", e.kernel().toString());
        assertEquals(Int32.of(-5), e.varAtName("x").valueOrVarSet());

        e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_5))
            .setSource("x = --a")
            .perform();
        assertEquals("x = --a", e.sntcOrExpr().toString());
        expected = """
            local $v0 in
                $negate(a, $v0)
                $negate($v0, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(5), e.varAtName("x").valueOrVarSet());

        e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_5))
            .setSource("x = ---a")
            .perform();
        assertEquals("x = ---a", e.sntcOrExpr().toString());
        expected = """
            local $v0 in
                local $v1 in
                    $negate(a, $v1)
                    $negate($v1, $v0)
                end
                $negate($v0, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.of(-5), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testInt64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = 1L")
            .perform();
        assertEquals("x = 1L", e.sntcOrExpr().toString());
        assertEquals("$bind(1L, x)", e.kernel().toString());
        assertEquals(Int64.I64_1, e.varAtName("x").valueOrVarSet());

        e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = -1L")
            .perform();
        assertEquals("x = -1L", e.sntcOrExpr().toString());
        assertEquals("$negate(1L, x)", e.kernel().toString());
        assertEquals(Int64.of(-1), e.varAtName("x").valueOrVarSet());

        e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = --1L")
            .perform();
        assertEquals("x = --1L", e.sntcOrExpr().toString());
        String expected = """
            local $v0 in
                $negate(1L, $v0)
                $negate($v0, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int64.I64_1, e.varAtName("x").valueOrVarSet());

        e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = ---1L")
            .perform();
        assertEquals("x = ---1L", e.sntcOrExpr().toString());
        expected = """
            local $v0 in
                local $v1 in
                    $negate(1L, $v1)
                    $negate($v1, $v0)
                end
                $negate($v0, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int64.of(-1), e.varAtName("x").valueOrVarSet());

        e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.I64_5))
            .setSource("x = a")
            .perform();
        assertEquals("x = a", e.sntcOrExpr().toString());
        assertEquals("$bind(a, x)", e.kernel().toString());
        assertEquals(Int64.of(5), e.varAtName("x").valueOrVarSet());

        e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.I64_5))
            .setSource("x = -a")
            .perform();
        assertEquals("x = -a", e.sntcOrExpr().toString());
        assertEquals("$negate(a, x)", e.kernel().toString());
        assertEquals(Int64.of(-5), e.varAtName("x").valueOrVarSet());

        e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.I64_5))
            .setSource("x = --a")
            .perform();
        assertEquals("x = --a", e.sntcOrExpr().toString());
        expected = """
            local $v0 in
                $negate(a, $v0)
                $negate($v0, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int64.of(5), e.varAtName("x").valueOrVarSet());

        e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.I64_5))
            .setSource("x = ---a")
            .perform();
        assertEquals("x = ---a", e.sntcOrExpr().toString());
        expected = """
            local $v0 in
                local $v1 in
                    $negate(a, $v1)
                    $negate($v1, $v0)
                end
                $negate($v0, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int64.of(-5), e.varAtName("x").valueOrVarSet());
    }

}
