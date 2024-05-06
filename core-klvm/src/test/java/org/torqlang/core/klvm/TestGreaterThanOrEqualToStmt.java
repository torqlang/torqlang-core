/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.jupiter.api.Test;
import org.torqlang.core.util.SourceSpan;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestGreaterThanOrEqualToStmt {

    @Test
    public void testCompleteGreaterThanOrEqualToComplete() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Int32 a = Int32.I32_1;
        Int32 b = Int32.I32_3;
        Ident x = Ident.create("x");
        Var xVar = new Var();

        GreaterThanOrEqualToStmt greaterThanOrEqualTo = new GreaterThanOrEqualToStmt(a, b, x, emptySpan);
        assertEquals(a, greaterThanOrEqualTo.a());
        assertEquals(b, greaterThanOrEqualTo.b());
        assertEquals(x, greaterThanOrEqualTo.x());
        Env env = Env.create(new EnvEntry(x, xVar));
        greaterThanOrEqualTo.compute(env, null);
        assertEquals(Bool.FALSE, x.resolveValue(env));
        assertEquals("$ge(1, 3, x)", greaterThanOrEqualTo.toString());

        Set<Ident> knownBound = new HashSet<>();
        Set<Ident> lexicallyFree = new HashSet<>();
        greaterThanOrEqualTo.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(0, knownBound.size());
        assertEquals(1, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(x));
    }

    @Test
    public void testCompleteGreaterThanOrEqualToIdent() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Int32 a = Int32.I32_1;
        Ident b = Ident.create("b");
        Var bVar = new Var();
        Ident x = Ident.create("x");
        Var xVar = new Var();

        GreaterThanOrEqualToStmt greaterThanOrEqualTo = new GreaterThanOrEqualToStmt(a, b, x, emptySpan);
        assertEquals(a, greaterThanOrEqualTo.a());
        assertEquals(b, greaterThanOrEqualTo.b());
        assertEquals(x, greaterThanOrEqualTo.x());
        Env env = Env.create(new EnvEntry(b, bVar), new EnvEntry(x, xVar));
        bVar.bindToValue(Int32.I32_3, null);
        greaterThanOrEqualTo.compute(env, null);
        assertEquals(Bool.FALSE, x.resolveValue(env));
        assertEquals("$ge(1, b, x)", greaterThanOrEqualTo.toString());

        Set<Ident> knownBound = new HashSet<>();
        Set<Ident> lexicallyFree = new HashSet<>();
        greaterThanOrEqualTo.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(0, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(b));
        assertTrue(lexicallyFree.contains(x));
    }

    @Test
    public void testIdentGreaterThanOrEqualToComplete() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Ident a = Ident.create("a");
        Var aVar = new Var();
        Int32 b = Int32.I32_3;
        Ident x = Ident.create("x");
        Var xVar = new Var();

        GreaterThanOrEqualToStmt greaterThanOrEqualTo = new GreaterThanOrEqualToStmt(a, b, x, emptySpan);
        assertEquals(a, greaterThanOrEqualTo.a());
        assertEquals(b, greaterThanOrEqualTo.b());
        assertEquals(x, greaterThanOrEqualTo.x());
        Env env = Env.create(new EnvEntry(a, aVar), new EnvEntry(x, xVar));
        aVar.bindToValue(Int32.I32_1, null);
        greaterThanOrEqualTo.compute(env, null);
        assertEquals(Bool.FALSE, x.resolveValue(env));
        assertEquals("$ge(a, 3, x)", greaterThanOrEqualTo.toString());

        Set<Ident> knownBound = new HashSet<>();
        Set<Ident> lexicallyFree = new HashSet<>();
        greaterThanOrEqualTo.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(0, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(a));
        assertTrue(lexicallyFree.contains(x));
    }

    @Test
    public void testIdentGreaterThanOrEqualToIdent() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Ident a = Ident.create("a");
        Var aVar = new Var();
        Ident b = Ident.create("b");
        Var bVar = new Var();
        Ident x = Ident.create("x");
        Var xVar = new Var();

        GreaterThanOrEqualToStmt greaterThanOrEqualTo = new GreaterThanOrEqualToStmt(a, b, x, emptySpan);
        assertEquals(a, greaterThanOrEqualTo.a());
        assertEquals(b, greaterThanOrEqualTo.b());
        assertEquals(x, greaterThanOrEqualTo.x());
        Env env = Env.create(List.of(new EnvEntry(a, aVar), new EnvEntry(b, bVar), new EnvEntry(x, xVar)));
        aVar.bindToValue(Int32.I32_1, null);
        bVar.bindToValue(Int32.I32_3, null);
        greaterThanOrEqualTo.compute(env, null);
        assertEquals(Bool.FALSE, x.resolveValue(env));
        assertEquals("$ge(a, b, x)", greaterThanOrEqualTo.toString());

        Set<Ident> knownBound = new HashSet<>();
        Set<Ident> lexicallyFree = new HashSet<>();
        greaterThanOrEqualTo.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(0, knownBound.size());
        assertEquals(3, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(a));
        assertTrue(lexicallyFree.contains(b));
        assertTrue(lexicallyFree.contains(x));

        knownBound = new HashSet<>();
        knownBound.add(a);
        lexicallyFree = new HashSet<>();
        greaterThanOrEqualTo.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(1, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(b));
        assertTrue(lexicallyFree.contains(x));

        knownBound = new HashSet<>();
        knownBound.add(b);
        lexicallyFree = new HashSet<>();
        greaterThanOrEqualTo.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(1, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(a));
        assertTrue(lexicallyFree.contains(x));

        knownBound = new HashSet<>();
        knownBound.add(x);
        lexicallyFree = new HashSet<>();
        greaterThanOrEqualTo.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(1, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(a));
        assertTrue(lexicallyFree.contains(b));

        knownBound = new HashSet<>();
        knownBound.add(a);
        knownBound.add(b);
        knownBound.add(x);
        lexicallyFree = new HashSet<>();
        greaterThanOrEqualTo.captureLexicallyFree(knownBound, lexicallyFree);
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

        GreaterThanOrEqualToStmt greaterThanOrEqualTo = new GreaterThanOrEqualToStmt(a, b, x, emptySpan);
        assertEquals(a, greaterThanOrEqualTo.a());
        assertEquals(b, greaterThanOrEqualTo.b());
        assertEquals(x, greaterThanOrEqualTo.x());
        Env env = Env.create(List.of(new EnvEntry(a, aVar), new EnvEntry(b, bVar), new EnvEntry(x, xVar)));
        bVar.bindToValue(Int32.I32_3, null);
        {
            WaitVarException exc = assertThrows(WaitVarException.class, () -> greaterThanOrEqualTo.compute(env, null));
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

        GreaterThanOrEqualToStmt greaterThanOrEqualTo = new GreaterThanOrEqualToStmt(a, b, x, emptySpan);
        assertEquals(a, greaterThanOrEqualTo.a());
        assertEquals(b, greaterThanOrEqualTo.b());
        assertEquals(x, greaterThanOrEqualTo.x());
        Env env = Env.create(List.of(new EnvEntry(a, aVar), new EnvEntry(b, bVar), new EnvEntry(x, xVar)));
        aVar.bindToValue(Int32.I32_1, null);
        {
            WaitVarException exc = assertThrows(WaitVarException.class, () -> greaterThanOrEqualTo.compute(env, null));
            assertNull(exc.getMessage());
            assertEquals(bVar, exc.barrier());
        }
    }

}
