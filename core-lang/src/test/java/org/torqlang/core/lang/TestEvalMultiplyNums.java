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

public class TestEvalMultiplyNums {

    @Test
    public void testInt32AndInt32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = 3 * 5")
            .perform();
        assertEquals("x = 3 * 5", e.sntcOrExpr().toString());
        assertEquals("$mult(3, 5, x)", e.kernel().toString());
        assertEquals(Int32.of(15), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testInt32AndVar32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_5))
            .setSource("x = 3 * a")
            .perform();
        assertEquals("x = 3 * a", e.sntcOrExpr().toString());
        assertEquals("$mult(3, a, x)", e.kernel().toString());
        assertEquals(Int32.of(15), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testInt64AndInt64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = 3L * 5L")
            .perform();
        assertEquals("x = 3L * 5L", e.sntcOrExpr().toString());
        assertEquals("$mult(3L, 5L, x)", e.kernel().toString());
        assertEquals(Int64.of(15), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(15), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testInt64AndVar64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.I64_5))
            .setSource("x = 3L * a")
            .perform();
        assertEquals("x = 3L * a", e.sntcOrExpr().toString());
        assertEquals("$mult(3L, a, x)", e.kernel().toString());
        assertEquals(Int64.of(15), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(15), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar32AndInt32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_5))
            .setSource("x = a * 3")
            .perform();
        assertEquals("x = a * 3", e.sntcOrExpr().toString());
        assertEquals("$mult(a, 3, x)", e.kernel().toString());
        assertEquals(Int32.of(15), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar32AndVar32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_3))
            .addVar(Ident.create("b"), new Var(Int32.I32_5))
            .setSource("x = a * b")
            .perform();
        assertEquals("x = a * b", e.sntcOrExpr().toString());
        assertEquals("$mult(a, b, x)", e.kernel().toString());
        assertEquals(Int32.of(15), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar32AndVar64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_3))
            .addVar(Ident.create("b"), new Var(Int64.I64_5))
            .setSource("x = a * b")
            .perform();
        assertEquals("x = a * b", e.sntcOrExpr().toString());
        assertEquals("$mult(a, b, x)", e.kernel().toString());
        assertEquals(Int64.of(15), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(15), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar64AndInt64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.I64_5))
            .setSource("x = a * 3L")
            .perform();
        assertEquals("x = a * 3L", e.sntcOrExpr().toString());
        assertEquals("$mult(a, 3L, x)", e.kernel().toString());
        assertEquals(Int64.of(15), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(15), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar64AndVar32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.I64_3))
            .addVar(Ident.create("b"), new Var(Int32.I32_5))
            .setSource("x = a * b")
            .perform();
        assertEquals("x = a * b", e.sntcOrExpr().toString());
        assertEquals("$mult(a, b, x)", e.kernel().toString());
        assertEquals(Int64.of(15), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(15), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar64AndVar64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.I64_3))
            .addVar(Ident.create("b"), new Var(Int64.I64_5))
            .setSource("x = a * b")
            .perform();
        assertEquals("x = a * b", e.sntcOrExpr().toString());
        assertEquals("$mult(a, b, x)", e.kernel().toString());
        assertEquals(Int64.of(15), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(15), e.varAtName("x").valueOrVarSet());
    }

}
