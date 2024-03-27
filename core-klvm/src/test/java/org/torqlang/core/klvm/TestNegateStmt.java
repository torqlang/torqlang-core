/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.Test;
import org.torqlang.core.util.SourceSpan;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestNegateStmt {

    @Test
    public void testNegateComplete() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Int32 a = Int32.I32_5;
        Ident x = Ident.create("x");
        Var xVar = new Var();

        NegateStmt negate = new NegateStmt(a, x, emptySpan);
        assertEquals(a, negate.a());
        assertEquals(x, negate.x());
        Env env = Env.create(new EnvEntry(x, xVar));
        negate.compute(env, null);
        assertEquals(Int32.of(-5), x.resolveValue(env));
        assertEquals("$negate(5, x)", negate.toString());

        Set<Ident> knownBound = new HashSet<>();
        Set<Ident> lexicallyFree = new HashSet<>();
        negate.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(0, knownBound.size());
        assertEquals(1, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(x));

        Int32 minusA = Int32.of(-5);
        negate = new NegateStmt(minusA, x, emptySpan);
        assertEquals(minusA, negate.a());
        assertEquals(x, negate.x());
        xVar = new Var();
        env = Env.create(new EnvEntry(x, xVar));
        negate.compute(env, null);
        assertEquals(Int32.of(5), x.resolveValue(env));
        assertEquals("$negate(-5, x)", negate.toString());
    }

    @Test
    public void testNegateIdent() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Ident a = Ident.create("a");
        Var aVar = new Var();
        Ident x = Ident.create("x");
        Var xVar = new Var();

        NegateStmt negate = new NegateStmt(a, x, emptySpan);
        assertEquals(a, negate.a());
        assertEquals(x, negate.x());
        Env env = Env.create(new EnvEntry(a, aVar), new EnvEntry(x, xVar));
        aVar.bindToValue(Int32.I32_5, null);
        negate.compute(env, null);
        assertEquals(Int32.of(-5), x.resolveValue(env));
        assertEquals("$negate(a, x)", negate.toString());

        Set<Ident> knownBound = new HashSet<>();
        Set<Ident> lexicallyFree = new HashSet<>();
        negate.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(0, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(a));
        assertTrue(lexicallyFree.contains(x));

        knownBound = new HashSet<>();
        knownBound.add(a);
        lexicallyFree = new HashSet<>();
        negate.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(1, knownBound.size());
        assertEquals(1, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(x));

        knownBound = new HashSet<>();
        knownBound.add(a);
        knownBound.add(x);
        lexicallyFree = new HashSet<>();
        negate.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(2, knownBound.size());
        assertEquals(0, lexicallyFree.size());
    }

}
