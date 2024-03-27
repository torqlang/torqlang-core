/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.*;

import static org.junit.Assert.assertEquals;

public class TestEvalLiteralExprs {

    @Test
    public void testBool() throws Exception {
        String source = """
            begin
                true
            end""";
        Ident x = Ident.create("x");
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(x)
            .setExprIdent(x)
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = "$bind(true, x)";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Bool.TRUE, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testChar() throws Exception {
        String source = """
            begin
                &c
            end""";
        Ident x = Ident.create("x");
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(x)
            .setExprIdent(x)
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = "$bind(&c, x)";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Char.of('c'), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testEof() throws Exception {
        String source = """
            begin
                eof
            end""";
        Ident x = Ident.create("x");
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(x)
            .setExprIdent(x)
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = "$bind(eof, x)";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Eof.SINGLETON, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testFlt32() throws Exception {
        String source = """
            begin
                1.0f
            end""";
        Ident x = Ident.create("x");
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(x)
            .setExprIdent(x)
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = "$bind(1.0f, x)";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Flt32.of(1.0f), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testFlt64() throws Exception {
        String source = """
            begin
                1.0
            end""";
        Ident x = Ident.create("x");
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(x)
            .setExprIdent(x)
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = "$bind(1.0, x)";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Flt64.of(1.0), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testInt32() throws Exception {
        String source = """
            begin
                1
            end""";
        Ident x = Ident.create("x");
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(x)
            .setExprIdent(x)
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = "$bind(1, x)";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int32.I32_1, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testInt64() throws Exception {
        String source = """
            begin
                1L
            end""";
        Ident x = Ident.create("x");
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(x)
            .setExprIdent(x)
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = "$bind(1L, x)";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Int64.I64_1, e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testStr() throws Exception {
        String source = """
            begin
                'my-value'
            end""";
        Ident x = Ident.create("x");
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(x)
            .setExprIdent(x)
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = "$bind('my-value', x)";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Str.of("my-value"), e.varAtName("x").valueOrVarSet());
    }

    @Test
    public void testUnit() throws Exception {
        String source = """
            begin
                nothing
            end""";
        Ident x = Ident.create("x");
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(x)
            .setExprIdent(x)
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = "$bind(nothing, x)";
        assertEquals(expected, e.kernel().toString());
        assertEquals(Nothing.SINGLETON, e.varAtName("x").valueOrVarSet());
    }

}
