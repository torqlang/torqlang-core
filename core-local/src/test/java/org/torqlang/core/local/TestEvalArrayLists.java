/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.local;

import org.junit.Test;
import org.torqlang.core.klvm.*;
import org.torqlang.core.lang.Evaluator;
import org.torqlang.core.lang.EvaluatorPerformed;
import org.torqlang.core.local.ArrayListMod.ArrayListObj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestEvalArrayLists {

    @Test
    public void testAdd() throws Exception {
        String source = """
            begin
                x = ArrayList.new()
                x.add(3)
                x.add(2)
                x.add(1)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(ArrayListMod.ARRAY_LIST_IDENT, new Var(ArrayListMod.ARRAY_LIST_CLS))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            $select_apply(ArrayList, ['new'], x)
            $select_apply(x, ['add'], 3)
            $select_apply(x, ['add'], 2)
            $select_apply(x, ['add'], 1)""";
        assertEquals(expected, e.kernel().toString());
        assertTrue(e.varAtName("x").valueOrVarSet() instanceof ArrayListObj);
        ArrayListObj x = (ArrayListObj) e.varAtName("x").valueOrVarSet();
        assertEquals(3, x.state.size());
        assertEquals(Int32.of(3), x.state.get(0).resolveValue());
        assertEquals(Int32.of(2), x.state.get(1).resolveValue());
        assertEquals(Int32.of(1), x.state.get(2).resolveValue());
    }

    @Test
    public void testClear() throws Exception {
        String source = """
            begin
                x = ArrayList.new()
                x.add(3)
                x.add(2)
                x.add(1)
                x.clear()
                x.add(0)
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(ArrayListMod.ARRAY_LIST_IDENT, new Var(ArrayListMod.ARRAY_LIST_CLS))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            $select_apply(ArrayList, ['new'], x)
            $select_apply(x, ['add'], 3)
            $select_apply(x, ['add'], 2)
            $select_apply(x, ['add'], 1)
            $select_apply(x, ['clear'])
            $select_apply(x, ['add'], 0)""";
        assertEquals(expected, e.kernel().toString());
        assertTrue(e.varAtName("x").valueOrVarSet() instanceof ArrayListObj);
        ArrayListObj x = (ArrayListObj) e.varAtName("x").valueOrVarSet();
        assertEquals(1, x.state.size());
        assertEquals(Int32.of(0), x.state.get(0).resolveValue());
    }

    @Test
    public void testToTuple() throws Exception {
        String source = """
            begin
                var a = [3, 2, 1]
                var b = ArrayList.new(a)
                x = b.to_tuple()
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(ArrayListMod.ARRAY_LIST_IDENT, new Var(ArrayListMod.ARRAY_LIST_CLS))
            .addVar(Ident.create("x"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local a, b in
                $bind([3, 2, 1], a)
                $select_apply(ArrayList, ['new'], a, b)
                $select_apply(b, ['to_tuple'], x)
            end""";
        assertEquals(expected, e.kernel().toString());
        CompleteTuple expectedTuple = Rec.completeTupleBuilder()
            .addValue(Int32.I32_3)
            .addValue(Int32.I32_2)
            .addValue(Int32.I32_1)
            .build();
        assertTrue(e.varAtName("x").valueOrVarSet() instanceof Rec);
        Rec xRec = (Rec) e.varAtName("x").valueOrVarSet();
        assertTrue(expectedTuple.entails(xRec, null));
    }

}
