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

public class TestModuloStmt {

    @Test
    public void testCompleteModuloComplete() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Int32 a = Int32.I32_5;
        Int32 b = Int32.I32_3;
        Ident x = Ident.create("x");
        Var xVar = new Var();

        ModuloStmt modulo = new ModuloStmt(a, b, x, emptySpan);
        assertEquals(a, modulo.a());
        assertEquals(b, modulo.b());
        assertEquals(x, modulo.x());
        Env env = Env.create(new EnvEntry(x, xVar));
        modulo.compute(env, null);
        assertEquals(Int32.I32_2, x.resolveValue(env));
        assertEquals("$mod(5, 3, x)", modulo.toString());

        Set<Ident> knownBound = new HashSet<>();
        Set<Ident> lexicallyFree = new HashSet<>();
        modulo.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(0, knownBound.size());
        assertEquals(1, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(x));
    }

    @Test
    public void testCompleteModuloIdent() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Int32 a = Int32.I32_5;
        Ident b = Ident.create("b");
        Var bVar = new Var();
        Ident x = Ident.create("x");
        Var xVar = new Var();

        ModuloStmt modulo = new ModuloStmt(a, b, x, emptySpan);
        assertEquals(a, modulo.a());
        assertEquals(b, modulo.b());
        assertEquals(x, modulo.x());
        Env env = Env.create(new EnvEntry(b, bVar), new EnvEntry(x, xVar));
        bVar.bindToValue(Int32.I32_3, null);
        modulo.compute(env, null);
        assertEquals(Int32.I32_2, x.resolveValue(env));
        assertEquals("$mod(5, b, x)", modulo.toString());

        Set<Ident> knownBound = new HashSet<>();
        Set<Ident> lexicallyFree = new HashSet<>();
        modulo.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(0, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(b));
        assertTrue(lexicallyFree.contains(x));
    }

    @Test
    public void testIdentModuloComplete() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Ident a = Ident.create("a");
        Var aVar = new Var();
        Int32 b = Int32.I32_3;
        Ident x = Ident.create("x");
        Var xVar = new Var();

        ModuloStmt modulo = new ModuloStmt(a, b, x, emptySpan);
        assertEquals(a, modulo.a());
        assertEquals(b, modulo.b());
        assertEquals(x, modulo.x());
        Env env = Env.create(new EnvEntry(a, aVar), new EnvEntry(x, xVar));
        aVar.bindToValue(Int32.I32_5, null);
        modulo.compute(env, null);
        assertEquals(Int32.I32_2, x.resolveValue(env));
        assertEquals("$mod(a, 3, x)", modulo.toString());

        Set<Ident> knownBound = new HashSet<>();
        Set<Ident> lexicallyFree = new HashSet<>();
        modulo.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(0, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(a));
        assertTrue(lexicallyFree.contains(x));
    }

    @Test
    public void testIdentModuloIdent() throws Exception {

        SourceSpan emptySpan = SourceSpan.emptySourceSpan();
        Ident a = Ident.create("a");
        Var aVar = new Var();
        Ident b = Ident.create("b");
        Var bVar = new Var();
        Ident x = Ident.create("x");
        Var xVar = new Var();

        ModuloStmt modulo = new ModuloStmt(a, b, x, emptySpan);
        assertEquals(a, modulo.a());
        assertEquals(b, modulo.b());
        assertEquals(x, modulo.x());
        Env env = Env.create(List.of(new EnvEntry(a, aVar), new EnvEntry(b, bVar), new EnvEntry(x, xVar)));
        aVar.bindToValue(Int32.I32_5, null);
        bVar.bindToValue(Int32.I32_3, null);
        modulo.compute(env, null);
        assertEquals(Int32.I32_2, x.resolveValue(env));
        assertEquals("$mod(a, b, x)", modulo.toString());

        Set<Ident> knownBound = new HashSet<>();
        Set<Ident> lexicallyFree = new HashSet<>();
        modulo.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(0, knownBound.size());
        assertEquals(3, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(a));
        assertTrue(lexicallyFree.contains(b));
        assertTrue(lexicallyFree.contains(x));

        knownBound = new HashSet<>();
        knownBound.add(a);
        lexicallyFree = new HashSet<>();
        modulo.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(1, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(b));
        assertTrue(lexicallyFree.contains(x));

        knownBound = new HashSet<>();
        knownBound.add(b);
        lexicallyFree = new HashSet<>();
        modulo.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(1, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(a));
        assertTrue(lexicallyFree.contains(x));

        knownBound = new HashSet<>();
        knownBound.add(x);
        lexicallyFree = new HashSet<>();
        modulo.captureLexicallyFree(knownBound, lexicallyFree);
        assertEquals(1, knownBound.size());
        assertEquals(2, lexicallyFree.size());
        assertTrue(lexicallyFree.contains(a));
        assertTrue(lexicallyFree.contains(b));

        knownBound = new HashSet<>();
        knownBound.add(a);
        knownBound.add(b);
        knownBound.add(x);
        lexicallyFree = new HashSet<>();
        modulo.captureLexicallyFree(knownBound, lexicallyFree);
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

        ModuloStmt modulo = new ModuloStmt(a, b, x, emptySpan);
        assertEquals(a, modulo.a());
        assertEquals(b, modulo.b());
        assertEquals(x, modulo.x());
        Env env = Env.create(List.of(new EnvEntry(a, aVar), new EnvEntry(b, bVar), new EnvEntry(x, xVar)));
        bVar.bindToValue(Int32.I32_3, null);
        {
            WaitVarException exc = assertThrows(WaitVarException.class, () -> modulo.compute(env, null));
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

        ModuloStmt modulo = new ModuloStmt(a, b, x, emptySpan);
        assertEquals(a, modulo.a());
        assertEquals(b, modulo.b());
        assertEquals(x, modulo.x());
        Env env = Env.create(List.of(new EnvEntry(a, aVar), new EnvEntry(b, bVar), new EnvEntry(x, xVar)));
        aVar.bindToValue(Int32.I32_1, null);
        {
            WaitVarException exc = assertThrows(WaitVarException.class, () -> modulo.compute(env, null));
            assertNull(exc.getMessage());
            assertEquals(bVar, exc.barrier());
        }
    }

}
