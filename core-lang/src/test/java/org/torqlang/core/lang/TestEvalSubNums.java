/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Int32;
import org.torqlang.core.klvm.Int64;
import org.torqlang.core.klvm.Var;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestEvalSubNums {

    @Test
    public void testInt32AndInt32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = 1 - 2")
            .perform();
        assertEquals("x = 1 - 2", e.sntcOrExpr().toString());
        assertEquals("$sub(1, 2, x)", e.kernel().toString());
        assertEquals(Int32.of(-1), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testInt32AndVar32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_5))
            .setSource("x = 2 - a")
            .perform();
        assertEquals("x = 2 - a", e.sntcOrExpr().toString());
        assertEquals("$sub(2, a, x)", e.kernel().toString());
        assertEquals(Int32.of(-3), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testInt64AndInt64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = 1L - 2L")
            .perform();
        assertEquals("x = 1L - 2L", e.sntcOrExpr().toString());
        assertEquals("$sub(1L, 2L, x)", e.kernel().toString());
        assertEquals(Int64.of(-1), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(-1), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testInt64AndVar64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.I64_5))
            .setSource("x = 2L - a")
            .perform();
        assertEquals("x = 2L - a", e.sntcOrExpr().toString());
        assertEquals("$sub(2L, a, x)", e.kernel().toString());
        assertEquals(Int64.of(-3), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(-3), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar32AndInt32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_5))
            .setSource("x = a - 2")
            .perform();
        assertEquals("x = a - 2", e.sntcOrExpr().toString());
        assertEquals("$sub(a, 2, x)", e.kernel().toString());
        assertEquals(Int32.of(3), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar32AndVar32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_2))
            .addVar(Ident.create("b"), new Var(Int32.I32_5))
            .setSource("x = a - b")
            .perform();
        assertEquals("x = a - b", e.sntcOrExpr().toString());
        assertEquals("$sub(a, b, x)", e.kernel().toString());
        assertEquals(Int32.of(-3), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar32AndVar64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_2))
            .addVar(Ident.create("b"), new Var(Int64.I64_5))
            .setSource("x = a - b")
            .perform();
        assertEquals("x = a - b", e.sntcOrExpr().toString());
        assertEquals("$sub(a, b, x)", e.kernel().toString());
        assertEquals(Int64.of(-3), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(-3), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar64AndInt64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.I64_5))
            .setSource("x = a - 2L")
            .perform();
        assertEquals("x = a - 2L", e.sntcOrExpr().toString());
        assertEquals("$sub(a, 2L, x)", e.kernel().toString());
        assertEquals(Int64.of(3), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(3), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar64AndVar32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.I64_2))
            .addVar(Ident.create("b"), new Var(Int32.I32_5))
            .setSource("x = a - b")
            .perform();
        assertEquals("x = a - b", e.sntcOrExpr().toString());
        assertEquals("$sub(a, b, x)", e.kernel().toString());
        assertEquals(Int64.of(-3), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(-3), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar64AndVar64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.I64_2))
            .addVar(Ident.create("b"), new Var(Int64.I64_5))
            .setSource("x = a - b")
            .perform();
        assertEquals("x = a - b", e.sntcOrExpr().toString());
        assertEquals("$sub(a, b, x)", e.kernel().toString());
        assertEquals(Int64.of(-3), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(-3), e.varAtName("x").valueOrVarSet());
    }

}
