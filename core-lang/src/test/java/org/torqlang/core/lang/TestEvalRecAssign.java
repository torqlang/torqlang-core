/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class TestEvalRecAssign {

    @Test
    public void test01() throws Exception {
        String source = """
            begin
                var r1 = {}
                var r2 = {}
                Rec.assign(r1, r2, x)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(RecPack.REC_IDENT, new Var(RecPack.REC_CLS))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local r1, r2 in
                $bind([], r1)
                $bind([], r2)
                $select_apply(Rec, ['assign'], r1, r2, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        Rec expectedRec = Rec.completeRecBuilder()
            .build();
        assertInstanceOf(Rec.class, e.varAtName("x").valueOrVarSet());
        Rec xRec = (Rec) e.varAtName("x").valueOrVarSet();
        assertEquals(expectedRec, xRec);
    }

    @Test
    public void test02() throws Exception {
        String source = """
            begin
                var r1 = {'a': 1}
                var r2 = {}
                Rec.assign(r1, r2, x)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(RecPack.REC_IDENT, new Var(RecPack.REC_CLS))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local r1, r2 in
                $bind({'a': 1}, r1)
                $bind([], r2)
                $select_apply(Rec, ['assign'], r1, r2, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        Rec expectedRec = Rec.completeRecBuilder()
            .addField(Str.of("a"), Int32.I32_1)
            .build();
        assertInstanceOf(Rec.class, e.varAtName("x").valueOrVarSet());
        Rec xRec = (Rec) e.varAtName("x").valueOrVarSet();
        assertEquals(expectedRec, xRec);
    }

    @Test
    public void test03() throws Exception {
        String source = """
            begin
                var r1 = {}
                var r2 = {'a': 1}
                Rec.assign(r1, r2, x)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(RecPack.REC_IDENT, new Var(RecPack.REC_CLS))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local r1, r2 in
                $bind([], r1)
                $bind({'a': 1}, r2)
                $select_apply(Rec, ['assign'], r1, r2, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        Rec expectedRec = Rec.completeRecBuilder()
            .addField(Str.of("a"), Int32.I32_1)
            .build();
        assertInstanceOf(Rec.class, e.varAtName("x").valueOrVarSet());
        Rec xRec = (Rec) e.varAtName("x").valueOrVarSet();
        assertEquals(expectedRec, xRec);
    }

    @Test
    public void test04() throws Exception {
        String source = """
            begin
                var r1 = {'a': 1}
                var r2 = {'a': 2}
                Rec.assign(r1, r2, x)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(RecPack.REC_IDENT, new Var(RecPack.REC_CLS))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local r1, r2 in
                $bind({'a': 1}, r1)
                $bind({'a': 2}, r2)
                $select_apply(Rec, ['assign'], r1, r2, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        Rec expectedRec = Rec.completeRecBuilder()
            .addField(Str.of("a"), Int32.I32_1)
            .build();
        assertInstanceOf(Rec.class, e.varAtName("x").valueOrVarSet());
        Rec xRec = (Rec) e.varAtName("x").valueOrVarSet();
        assertEquals(expectedRec, xRec);
    }

    @Test
    public void test05() throws Exception {
        String source = """
            begin
                var r1 = {'b': 4, 'c': 5}
                var r2 = {'a': 1, 'b': 2}
                Rec.assign(r1, r2, x)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(RecPack.REC_IDENT, new Var(RecPack.REC_CLS))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local r1, r2 in
                $bind({'b': 4, 'c': 5}, r1)
                $bind({'a': 1, 'b': 2}, r2)
                $select_apply(Rec, ['assign'], r1, r2, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        Rec expectedRec = Rec.completeRecBuilder()
            .addField(Str.of("a"), Int32.I32_1)
            .addField(Str.of("b"), Int32.I32_4)
            .addField(Str.of("c"), Int32.I32_5)
            .build();
        assertInstanceOf(Rec.class, e.varAtName("x").valueOrVarSet());
        Rec xRec = (Rec) e.varAtName("x").valueOrVarSet();
        assertEquals(expectedRec, xRec);
    }

    @Test
    public void test06() throws Exception {
        String source = """
            begin
                var r1 = {'b': 4, 'c': 5}
                var r2 = {'a': 1, 'b': 2}
                x = Rec.assign(r1, r2)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(RecPack.REC_IDENT, new Var(RecPack.REC_CLS))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local r1, r2 in
                $bind({'b': 4, 'c': 5}, r1)
                $bind({'a': 1, 'b': 2}, r2)
                $select_apply(Rec, ['assign'], r1, r2, x)
            end""";
        assertEquals(expected, e.kernel().toString());
        Rec expectedRec = Rec.completeRecBuilder()
            .addField(Str.of("a"), Int32.I32_1)
            .addField(Str.of("b"), Int32.I32_4)
            .addField(Str.of("c"), Int32.I32_5)
            .build();
        assertInstanceOf(Rec.class, e.varAtName("x").valueOrVarSet());
        Rec xRec = (Rec) e.varAtName("x").valueOrVarSet();
        assertEquals(expectedRec, xRec);
    }

}
