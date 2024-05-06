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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestEvalModuloNums {

    @Test
    public void testInt32AndInt32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = 7 % 5")
            .perform();
        assertEquals("x = 7 % 5", e.sntcOrExpr().toString());
        assertEquals("$mod(7, 5, x)", e.kernel().toString());
        assertEquals(Int32.of(2), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testInt32AndVar32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_5))
            .setSource("x = 7 % a")
            .perform();
        assertEquals("x = 7 % a", e.sntcOrExpr().toString());
        assertEquals("$mod(7, a, x)", e.kernel().toString());
        assertEquals(Int32.of(2), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testInt64AndInt64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .setSource("x = 7L % 5L")
            .perform();
        assertEquals("x = 7L % 5L", e.sntcOrExpr().toString());
        assertEquals("$mod(7L, 5L, x)", e.kernel().toString());
        assertEquals(Int64.of(2), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(2), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testInt64AndVar64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.I64_5))
            .setSource("x = 7L % a")
            .perform();
        assertEquals("x = 7L % a", e.sntcOrExpr().toString());
        assertEquals("$mod(7L, a, x)", e.kernel().toString());
        assertEquals(Int64.of(2), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(2), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar32AndInt32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_7))
            .setSource("x = a % 5")
            .perform();
        assertEquals("x = a % 5", e.sntcOrExpr().toString());
        assertEquals("$mod(a, 5, x)", e.kernel().toString());
        assertEquals(Int32.of(2), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar32AndVar32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.I32_7))
            .addVar(Ident.create("b"), new Var(Int32.of(5)))
            .setSource("x = a % b")
            .perform();
        assertEquals("x = a % b", e.sntcOrExpr().toString());
        assertEquals("$mod(a, b, x)", e.kernel().toString());
        assertEquals(Int32.of(2), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar32AndVar64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int32.of(7)))
            .addVar(Ident.create("b"), new Var(Int64.of(5)))
            .setSource("x = a % b")
            .perform();
        assertEquals("x = a % b", e.sntcOrExpr().toString());
        assertEquals("$mod(a, b, x)", e.kernel().toString());
        assertEquals(Int64.of(2), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(2), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar64AndInt64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.of(7)))
            .setSource("x = a % 5L")
            .perform();
        assertEquals("x = a % 5L", e.sntcOrExpr().toString());
        assertEquals("$mod(a, 5L, x)", e.kernel().toString());
        assertEquals(Int64.of(2), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(2), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar64AndVar32() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.of(7)))
            .addVar(Ident.create("b"), new Var(Int32.of(5)))
            .setSource("x = a % b")
            .perform();
        assertEquals("x = a % b", e.sntcOrExpr().toString());
        assertEquals("$mod(a, b, x)", e.kernel().toString());
        assertEquals(Int64.of(2), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(2), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testVar64AndVar64() throws Exception {
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"), new Var(Int64.of(7)))
            .addVar(Ident.create("b"), new Var(Int64.of(5)))
            .setSource("x = a % b")
            .perform();
        assertEquals("x = a % b", e.sntcOrExpr().toString());
        assertEquals("$mod(a, b, x)", e.kernel().toString());
        assertEquals(Int64.of(2), e.varAtName("x").valueOrVarSet());
        assertNotEquals(Int32.of(2), e.varAtName("x").valueOrVarSet());
    }

}
