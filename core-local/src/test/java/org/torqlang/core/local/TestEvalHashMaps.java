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
import org.torqlang.core.local.HashMapMod.HashMapObj;

import static org.junit.Assert.*;
import static org.torqlang.core.local.CommonTools.stripCircularSpecifics;

public class TestEvalHashMaps {

    @Test
    public void test() throws Exception {
        String source = """
            begin
                x = HashMap.new()
                x.put(['one', 'two'], 'My key is a record!')
                y = ['one', 'two']
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(HashMapMod.HASH_MAP_IDENT, new Var(HashMapMod.HASH_MAP_CLS))
            .addVar(Ident.create("x"))
            .addVar(Ident.create("y"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            $select_apply(HashMap, ['new'], x)
            local $v0 in
                $bind(['one', 'two'], $v0)
                $select_apply(x, ['put'], $v0, 'My key is a record!')
            end
            $bind(['one', 'two'], y)""";
        assertEquals(expected, e.kernel().toString());
        assertTrue(e.varAtName("x").valueOrVarSet() instanceof HashMapObj);
        HashMapObj x = (HashMapObj) e.varAtName("x").valueOrVarSet();
        assertTrue(e.varAtName("y").valueOrVarSet() instanceof Rec);
        CompleteRec y = (CompleteRec) e.varAtName("y").valueOrVarSet();
        assertEquals(Str.of("My key is a record!"), x.state().get(y));
    }

    @Test
    public void testIndirectRefWithEquals() throws Exception {
        String source = """
            begin
                a = {'next': b}
                b = {'next': a}
                x = HashMap.new()
                x.put(a, 'My key is circular!')
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(HashMapMod.HASH_MAP_IDENT, new Var(HashMapMod.HASH_MAP_CLS))
            .addVar(Ident.create("x"))
            .addVar(Ident.create("a"))
            .addVar(Ident.create("b"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            $create_rec({'next': b}, a)
            $create_rec({'next': a}, b)
            $select_apply(HashMap, ['new'], x)
            $select_apply(x, ['put'], a, 'My key is circular!')""";
        assertEquals(expected, e.kernel().toString());
        assertTrue(e.varAtName("x").valueOrVarSet() instanceof HashMapObj);
        HashMapObj x = (HashMapObj) e.varAtName("x").valueOrVarSet();
        CompleteRec completeRec = (CompleteRec) x.state().keySet().iterator().next();
        assertTrue(e.varAtName("a").valueOrVarSet() instanceof Rec);
        Rec a = (Rec) e.varAtName("a").valueOrVarSet();
        assertTrue(e.varAtName("b").valueOrVarSet() instanceof Rec);
        Rec b = (Rec) e.varAtName("b").valueOrVarSet();
        // This test contains two partial records that reference each other, `a` and `b`.
        // Record `a` was used as a key, which caused it to be converted to a complete record.
        // Ultimately, the key to the hash map, value `a`, and value `b` are all equal in value.
        assertNotEquals(a, b);
        assertTrue(completeRec.entails(a, null));
        assertTrue(completeRec.entails(b, null));
        assertEquals(x.state().get(b.checkComplete()), Str.of("My key is circular!"));
        // Circular references specially formatted
        assertEquals("{'next': {'next': <<$circular>>}}", stripCircularSpecifics(a.toString()));
    }

    @Test
    public void testValueIter() throws Exception {
        String source = """
            begin
                var hm = HashMap.new()
                hm.put('0-key', 'Zero')
                hm.put('1-key', 'One')
                var value_iter = ValueIter.new(hm)
                x = value_iter()
                y = value_iter()
                z = value_iter()
            end""";
        EvaluatorPerformed e = Evaluator.builder()
            .addVar(HashMapMod.HASH_MAP_IDENT, new Var(HashMapMod.HASH_MAP_CLS))
            .addVar(ValueIterMod.VALUE_ITER_IDENT, new Var(ValueIterMod.VALUE_ITER_CLS))
            .addVar(Ident.create("x"))
            .addVar(Ident.create("y"))
            .addVar(Ident.create("z"))
            .setSource(source)
            .perform();
        assertEquals(source, e.sntcOrExpr().toString());
        String expected = """
            local hm, value_iter in
                $select_apply(HashMap, ['new'], hm)
                $select_apply(hm, ['put'], '0-key', 'Zero')
                $select_apply(hm, ['put'], '1-key', 'One')
                $select_apply(ValueIter, ['new'], hm, value_iter)
                value_iter(x)
                value_iter(y)
                value_iter(z)
            end""";
        assertEquals(expected, e.kernel().toString());
        ValueOrVar x = e.varAtName("x").resolveValueOrVar();
        assertTrue(x.equals(Str.of("Zero")) || x.equals(Str.of("One")));
        ValueOrVar y = e.varAtName("y").resolveValueOrVar();
        assertTrue(y.equals(Str.of("Zero")) || y.equals(Str.of("One")));
        ValueOrVar z = e.varAtName("z").resolveValueOrVar();
        assertEquals(Eof.SINGLETON, z);
    }

}