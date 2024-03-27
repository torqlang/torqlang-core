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

import static org.junit.Assert.*;

public class TestLessThanStmt {

    @Test
    public void testCompleteLessThanComplete() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Int32 a = Int32.I32_1;
        Int32 b = Int32.I32_3;
        Ident x = Ident.create("x");
        Var xVar = new Var();

        LessThanStmt lessThan = new LessThanStmt(a, b, x, emptySpan);
        assertEquals(a, lessThan.a());
        assertEquals(b, lessThan.b());
        assertEquals(x, lessThan.x());
        Env env = Env.create(new EnvEntry(x, xVar));
        lessThan.compute(env, null);
        assertEquals(Bool.TRUE, x.resolveValue(env));
        assertEquals("$lt(1, 3, x)", lessThan.toString());

        Set<Ident> knownBound = new HashSet<>();
        Set<Ident> lexicallyFree = new HashSet<>();
        lessThan.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(0, knownBound.size());
        assertEquals(1, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(x));
    }

    @Test
    public void testCompleteLessThanIdent() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Int32 a = Int32.I32_1;
        Ident b = Ident.create("b");
        Var bVar = new Var();
        Ident x = Ident.create("x");
        Var xVar = new Var();

        LessThanStmt lessThan = new LessThanStmt(a, b, x, emptySpan);
        assertEquals(a, lessThan.a());
        assertEquals(b, lessThan.b());
        assertEquals(x, lessThan.x());
        Env env = Env.create(new EnvEntry(b, bVar), new EnvEntry(x, xVar));
        bVar.bindToValue(Int32.I32_3, null);
        lessThan.compute(env, null);
        assertEquals(Bool.TRUE, x.resolveValue(env));
        assertEquals("$lt(1, b, x)", lessThan.toString());

        Set<Ident> knownBound = new HashSet<>();
        Set<Ident> lexicallyFree = new HashSet<>();
        lessThan.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(0, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(b));
        assertTrue(lexicallyFree.contains(x));
    }

    @Test
    public void testIdentLessThanComplete() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Ident a = Ident.create("a");
        Var aVar = new Var();
        Int32 b = Int32.I32_3;
        Ident x = Ident.create("x");
        Var xVar = new Var();

        LessThanStmt lessThan = new LessThanStmt(a, b, x, emptySpan);
        assertEquals(a, lessThan.a());
        assertEquals(b, lessThan.b());
        assertEquals(x, lessThan.x());
        Env env = Env.create(new EnvEntry(a, aVar), new EnvEntry(x, xVar));
        aVar.bindToValue(Int32.I32_1, null);
        lessThan.compute(env, null);
        assertEquals(Bool.TRUE, x.resolveValue(env));
        assertEquals("$lt(a, 3, x)", lessThan.toString());

        Set<Ident> knownBound = new HashSet<>();
        Set<Ident> lexicallyFree = new HashSet<>();
        lessThan.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(0, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(a));
        assertTrue(lexicallyFree.contains(x));
    }

    @Test
    public void testIdentLessThanIdent() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Ident a = Ident.create("a");
        Var aVar = new Var();
        Ident b = Ident.create("b");
        Var bVar = new Var();
        Ident x = Ident.create("x");
        Var xVar = new Var();

        LessThanStmt lessThan = new LessThanStmt(a, b, x, emptySpan);
        assertEquals(a, lessThan.a());
        assertEquals(b, lessThan.b());
        assertEquals(x, lessThan.x());
        Env env = Env.create(List.of(new EnvEntry(a, aVar), new EnvEntry(b, bVar), new EnvEntry(x, xVar)));
        aVar.bindToValue(Int32.I32_1, null);
        bVar.bindToValue(Int32.I32_3, null);
        lessThan.compute(env, null);
        assertEquals(Bool.TRUE, x.resolveValue(env));
        assertEquals("$lt(a, b, x)", lessThan.toString());

        Set<Ident> knownBound = new HashSet<>();
        Set<Ident> lexicallyFree = new HashSet<>();
        lessThan.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(0, knownBound.size());
        assertEquals(3, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(a));
        assertTrue(lexicallyFree.contains(b));
        assertTrue(lexicallyFree.contains(x));

        knownBound = new HashSet<>();
        knownBound.add(a);
        lexicallyFree = new HashSet<>();
        lessThan.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(1, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(b));
        assertTrue(lexicallyFree.contains(x));

        knownBound = new HashSet<>();
        knownBound.add(b);
        lexicallyFree = new HashSet<>();
        lessThan.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(1, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(a));
        assertTrue(lexicallyFree.contains(x));

        knownBound = new HashSet<>();
        knownBound.add(x);
        lexicallyFree = new HashSet<>();
        lessThan.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(1, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(a));
        assertTrue(lexicallyFree.contains(b));

        knownBound = new HashSet<>();
        knownBound.add(a);
        knownBound.add(b);
        knownBound.add(x);
        lexicallyFree = new HashSet<>();
        lessThan.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(3, knownBound.size());
        assertEquals(0, lexicallyFree.size());
    }

    @Test
    public void testWaitOnLeft() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Ident a = Ident.create("a");
        Var aVar = new Var();
        Ident b = Ident.create("b");
        Var bVar = new Var();
        Ident x = Ident.create("x");
        Var xVar = new Var();

        LessThanStmt lessThan = new LessThanStmt(a, b, x, emptySpan);
        assertEquals(a, lessThan.a());
        assertEquals(b, lessThan.b());
        assertEquals(x, lessThan.x());
        Env env = Env.create(List.of(new EnvEntry(a, aVar), new EnvEntry(b, bVar), new EnvEntry(x, xVar)));
        bVar.bindToValue(Int32.I32_3, null);
        {
            WaitVarException exc = assertThrows(WaitVarException.class, () -> lessThan.compute(env, null));
            assertNull(exc.getMessage());
            assertEquals(aVar, exc.barrier());
        }
    }

    @Test
    public void testWaitOnRight() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Ident a = Ident.create("a");
        Var aVar = new Var();
        Ident b = Ident.create("b");
        Var bVar = new Var();
        Ident x = Ident.create("x");
        Var xVar = new Var();

        LessThanStmt lessThan = new LessThanStmt(a, b, x, emptySpan);
        assertEquals(a, lessThan.a());
        assertEquals(b, lessThan.b());
        assertEquals(x, lessThan.x());
        Env env = Env.create(List.of(new EnvEntry(a, aVar), new EnvEntry(b, bVar), new EnvEntry(x, xVar)));
        aVar.bindToValue(Int32.I32_1, null);
        {
            WaitVarException exc = assertThrows(WaitVarException.class, () -> lessThan.compute(env, null));
            assertNull(exc.getMessage());
            assertEquals(bVar, exc.barrier());
        }
    }

}
