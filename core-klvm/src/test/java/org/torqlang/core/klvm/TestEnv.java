/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestEnv {

    @Test
    public void testAdd() {

        Ident a = Ident.create("a");
        Ident b = Ident.create("b");
        Str testStr = Str.of("test string");
        Env e1;
        Env e2;
        Var v;

        Exception exc;

        e1 = Env.create(new EnvEntry(a, new Var(testStr)));
        assertNull(e1.parentEnv());
        e2 = e1.add(new EnvEntry(b, new Var(Int32.I32_1)));
        assertNull(e2.parentEnv());
        v = e2.get(a);
        assertNotNull(v);
        assertEquals(testStr, v.valueOrVarSet());
        v = e2.get(b);
        assertNotNull(v);
        assertEquals(Int32.I32_1, v.valueOrVarSet());

        exc = assertThrows(DuplicateIdentError.class, () -> e2.add(new EnvEntry(a, new Var(testStr))));
        assertEquals("Duplicate ident", exc.getMessage());
    }

    @Test
    public void testCreate() {

        Ident a = Ident.create("a");
        Ident b = Ident.create("b");
        Str testStr = Str.of("test string");
        Env e;
        Env p1;
        Env p2;
        Var v;

        Exception exc;

        // Privately

        e = Env.createPrivatelyForKlvm(null, new EnvEntry[]{new EnvEntry(a, new Var(testStr))});
        v = e.get(a);
        assertNotNull(v);
        assertEquals(testStr, v.valueOrVarSet());

        // Empty parent

        e = Env.create(Env.emptyEnv(), List.of());
        assertEquals(Env.emptyEnv(), e);

        e = Env.create(Env.emptyEnv(), Map.of());
        assertEquals(Env.emptyEnv(), e);

        // Null parent, empty bindings

        e = Env.create(null, List.of());
        assertEquals(Env.emptyEnv(), e);

        e = Env.create(null, Map.of());
        assertEquals(Env.emptyEnv(), e);

        // Default parent, empty bindings

        e = Env.create(List.of());
        assertEquals(Env.emptyEnv(), e);

        e = Env.create(Map.of());
        assertEquals(Env.emptyEnv(), e);

        // Non-null parent, empty bindings

        p1 = Env.create((Env) null, new EnvEntry(a, new Var(testStr)));

        e = Env.create(p1, List.of());
        assertEquals(p1, e);

        e = Env.create(p1, Map.of());
        assertEquals(p1, e);

        // Null parent, non-empty bindings

        p1 = Env.create(null, List.of(new EnvEntry(a, new Var(testStr))));

        v = p1.get(a);
        assertNotNull(v);
        assertEquals(testStr, v.valueOrVarSet());

        p1 = Env.create(null, Map.of(a, new Var(testStr)));

        v = p1.get(a);
        assertNotNull(v);
        assertEquals(testStr, v.valueOrVarSet());

        p1 = Env.create((Env) null, new EnvEntry(a, new Var(testStr)));

        v = p1.get(a);
        assertNotNull(v);
        assertEquals(testStr, v.valueOrVarSet());

        p1 = Env.create(null, new EnvEntry(a, new Var(testStr)), new EnvEntry(b, new Var(Int32.I32_1)));

        v = p1.get(a);
        assertNotNull(v);
        assertEquals(testStr, v.valueOrVarSet());
        v = p1.get(b);
        assertNotNull(v);
        assertEquals(Int32.I32_1, v.valueOrVarSet());

        // Default parent, non-empty bindings

        p1 = Env.create(List.of(new EnvEntry(a, new Var(testStr))));

        v = p1.get(a);
        assertNotNull(v);
        assertEquals(testStr, v.valueOrVarSet());

        p1 = Env.create(Map.of(a, new Var(testStr)));

        v = p1.get(a);
        assertNotNull(v);
        assertEquals(testStr, v.valueOrVarSet());

        p1 = Env.create(new EnvEntry(a, new Var(testStr)));

        v = p1.get(a);
        assertNotNull(v);
        assertEquals(testStr, v.valueOrVarSet());

        p1 = Env.create(new EnvEntry(a, new Var(testStr)), new EnvEntry(b, new Var(Int32.I32_1)));

        v = p1.get(a);
        assertNotNull(v);
        assertEquals(testStr, v.valueOrVarSet());
        v = p1.get(b);
        assertNotNull(v);
        assertEquals(Int32.I32_1, v.valueOrVarSet());

        // Two levels

        p1 = Env.create(null, List.of(new EnvEntry(a, new Var(testStr))));
        p2 = Env.create(p1, List.of(new EnvEntry(b, new Var(Int32.I32_1))));

        v = p2.get(a);
        assertNotNull(v);
        assertEquals(testStr, v.valueOrVarSet());

        p1 = Env.create(null, Map.of(a, new Var(testStr)));
        p2 = Env.create(p1, Map.of(b, new Var(Int32.I32_1)));

        v = p2.get(a);
        assertNotNull(v);
        assertEquals(testStr, v.valueOrVarSet());

        p1 = Env.create((Env) null, new EnvEntry(a, new Var(testStr)));
        p2 = Env.create(p1, new EnvEntry(b, new Var(Int32.I32_1)));

        v = p2.get(a);
        assertNotNull(v);
        assertEquals(testStr, v.valueOrVarSet());

        p1 = Env.create((Env) null, new EnvEntry(a, new Var(testStr)));
        p2 = Env.create(p1, new EnvEntry(a, new Var(Int32.I32_0)), new EnvEntry(b, new Var(Int32.I32_1)));

        v = p2.get(a);
        assertNotNull(v);
        assertEquals(Int32.I32_0, v.valueOrVarSet());
        v = p2.get(b);
        assertNotNull(v);
        assertEquals(Int32.I32_1, v.valueOrVarSet());

        // Null pointer exceptions

        exc = assertThrows(NullPointerException.class, () -> Env.create((Env) null, (EnvEntry) null));
        assertNull(exc.getMessage());

        exc = assertThrows(NullPointerException.class, () -> Env.create(null, null, null));
        assertNull(exc.getMessage());

        exc = assertThrows(NullPointerException.class, () -> Env.create(null, new EnvEntry(a, new Var(testStr)), null));
        assertNull(exc.getMessage());

        exc = assertThrows(NullPointerException.class, () -> Env.create((EnvEntry) null));
        assertNull(exc.getMessage());

        exc = assertThrows(NullPointerException.class, () -> Env.create((EnvEntry) null, null));
        assertNull(exc.getMessage());

        exc = assertThrows(NullPointerException.class, () -> Env.create(new EnvEntry(a, new Var(testStr)), null));
        assertNull(exc.getMessage());
    }

    @Test
    public void testCollectIdents() {
        Env e1, e2;
        Ident a = Ident.create("a");
        Ident b = Ident.create("b");
        Ident c = Ident.create("c");
        Var v0 = new Var(Int32.I32_0);
        Var v1 = new Var(Int32.I32_1);
        Set<Ident> collected;

        e1 = Env.create(new EnvEntry(a, v0), new EnvEntry(b, v1));
        e2 = Env.create(e1, new EnvEntry(c, v0));
        collected = e2.collectIdents(v0);
        assertEquals(2, collected.size());
        assertTrue(collected.contains(a));
        assertTrue(collected.contains(c));
        collected = e2.collectIdents(v1);
        assertEquals(1, collected.size());
        assertTrue(collected.contains(b));
    }

    @Test
    public void testContains() {
        Env e1, e2;
        Ident a = Ident.create("a");
        Ident b = Ident.create("b");
        Ident c = Ident.create("c");
        Var v0 = new Var(Int32.I32_0);
        Var v1 = new Var(Int32.I32_1);

        e1 = Env.create(new EnvEntry(a, v0), new EnvEntry(b, v1));
        assertTrue(e1.contains(a));
        assertTrue(e1.contains(b));
        assertFalse(e1.contains(c));
        e2 = Env.create(e1, new EnvEntry(c, v0));
        assertTrue(e2.contains(a));
        assertTrue(e2.contains(b));
        assertTrue(e2.contains(c));
    }

    @Test
    public void testIterator() {
        Env e1, e2;
        Ident a = Ident.create("a");
        Ident b = Ident.create("b");
        Ident c = Ident.create("c");
        Var v0 = new Var(Int32.I32_0);
        Var v1 = new Var(Int32.I32_1);
        Set<Ident> collected;

        e1 = Env.create(new EnvEntry(a, v0), new EnvEntry(b, v1));
        e2 = Env.create(e1, new EnvEntry(c, v0));
        collected = e2.collectIdents(v0);
        assertEquals(2, collected.size());
        assertTrue(collected.contains(a));
        assertTrue(collected.contains(c));
        collected = e2.collectIdents(v1);
        assertEquals(1, collected.size());
        assertTrue(collected.contains(b));
    }

    @Test
    public void testSetRootEnv() {

        Ident a = Ident.create("a");
        Ident b = Ident.create("b");
        Ident c = Ident.create("c");
        Env e0, e1, e2;
        String toKernelString;
        String[] lines;

        e1 = Env.create(new EnvEntry(a, new Var(Int32.I32_0)));
        e2 = Env.create(e1, new EnvEntry(b, new Var(Int32.I32_1)));
        assertEquals(e1, e2.rootEnv());
        e0 = Env.create(new EnvEntry(a, new Var(Int32.I32_2)), new EnvEntry(c, new Var(Int32.I32_3)));
        e2 = e2.setRootEnv(e0);
        assertEquals(e0, e2.rootEnv());
        assertEquals(Int32.I32_0, e2.get(a).valueOrVarSet());
        assertEquals(Int32.I32_1, e2.get(b).valueOrVarSet());
        assertEquals(Int32.I32_3, e2.get(c).valueOrVarSet());
        toKernelString = e2.toKernelString();
        lines = toKernelString.split("\n");
        assertEquals(4, lines.length);
        assertTrue(lines[0].startsWith("// env[0]: b = <<$var "));
        assertTrue(lines[1].startsWith("// env[1]: a = <<$var "));
        assertTrue(lines[2].startsWith("// env[2]: a = <<$var "));
        assertTrue(lines[3].startsWith("// env[2]: c = <<$var "));
    }

    @Test
    public void testToString() {

        Ident a = Ident.create("a");
        Ident b = Ident.create("b");
        Ident c = Ident.create("c");
        Env e1;
        Env e2;
        String toString;
        String toKernelString;
        String[] lines;

        e1 = Env.create(new EnvEntry(a, new Var(Int32.I32_0)));
        toString = e1.toString();
        lines = toString.split("\n");
        assertEquals(1, lines.length);
        assertTrue(lines[0].startsWith("env[0]: a = <<$var "));

        e1 = Env.create(new EnvEntry(a, new Var(Int32.I32_0)), new EnvEntry(b, new Var(Int32.I32_0)));
        toString = e1.toString();
        lines = toString.split("\n");
        assertEquals(2, lines.length);
        assertTrue(lines[0].startsWith("env[0]: a = <<$var "));
        assertTrue(lines[1].startsWith("env[0]: b = <<$var "));

        e2 = Env.create(e1, new EnvEntry(c, new Var(Int32.I32_2)));
        toString = e2.toString();
        lines = toString.split("\n");
        assertEquals(3, lines.length);
        assertTrue(lines[0].startsWith("env[0]: c = <<$var "));
        assertTrue(lines[1].startsWith("env[1]: a = <<$var "));
        assertTrue(lines[2].startsWith("env[1]: b = <<$var "));

        toKernelString = e2.toKernelString();
        lines = toKernelString.split("\n");
        assertEquals(3, lines.length);
        assertTrue(lines[0].startsWith("// env[0]: c = <<$var "));
        assertTrue(lines[1].startsWith("// env[1]: a = <<$var "));
        assertTrue(lines[2].startsWith("// env[1]: b = <<$var "));
    }

}
