/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.jupiter.api.Test;
import org.torqlang.core.util.IntegerCounter;

import static org.junit.jupiter.api.Assertions.*;
import static org.torqlang.core.klvm.VarSet.EMPTY_VAR_SET;

public class TestVar {

    @Test
    public void testBindToValueOrVar() throws Exception {
        Var v1 = new Var();
        v1.bindToValueOrVar(Int32.I32_0, null);
        assertEquals(Int32.I32_0, v1.valueOrVarSet());
        Var v2 = new Var();
        Var v3 = new Var();
        v2.bindToValueOrVar(v3, null);
        assertEquals(v2.valueOrVarSet(), v3.valueOrVarSet());
    }

    @Test
    public void testBindToValueWithCallback() throws Exception {
        IntegerCounter counter = new IntegerCounter(0);
        Var v1 = new Var();
        BindCallback bindCallback = (var, value) -> counter.add(1);
        v1.setBindCallback(bindCallback);
        assertEquals(bindCallback, v1.bindCallback());
        assertEquals(EMPTY_VAR_SET, v1.valueOrVarSet());
        assertEquals(0, counter.get());
        v1.bindToValue(Int32.I32_0, null);
        assertEquals(Int32.I32_0, v1.valueOrVarSet());
        assertEquals(1, counter.get());
    }

    @Test
    public void testBindToValueWithNull() {
        Var v1 = new Var();
        Exception exc = assertThrows(NullPointerException.class, () -> v1.bindToValue(null, null));
        assertEquals("value", exc.getMessage());
    }

    @Test
    public void testBindToValueWithRightSideVarSetOf2() throws Exception {
        Str s = Str.of("test value");
        Var v1 = new Var();
        Var v2 = new Var();
        v1.bindToVar(v2, null);
        v1.bindToValue(s, null);
        assertEquals(s, v1.valueOrVarSet());
        assertEquals(s, v2.valueOrVarSet());
    }

    @Test
    public void testBindToValueWithRightSideVarSetOf3() throws Exception {
        Str s = Str.of("test value");
        Var v1 = new Var();
        Var v2 = new Var();
        Var v3 = new Var();
        v1.bindToVar(v2, null);
        v1.bindToVar(v3, null);
        v1.bindToValue(s, null);
        assertEquals(s, v1.valueOrVarSet());
        assertEquals(s, v2.valueOrVarSet());
        assertEquals(s, v3.valueOrVarSet());
    }

    @Test
    public void testBindToValueWithRightSideVarSetOf3AndCallback() throws Exception {
        IntegerCounter counter = new IntegerCounter(0);
        BindCallback bindCallback = (var, value) -> counter.add(1);
        Str s = Str.of("test value");
        Var v1 = new Var();
        v1.setBindCallback(bindCallback);
        Var v2 = new Var();
        v2.setBindCallback(bindCallback);
        Var v3 = new Var();
        v3.setBindCallback(bindCallback);
        v1.bindToVar(v2, null);
        v1.bindToVar(v3, null);
        assertEquals(0, counter.get());
        v2.bindToValue(s, null);
        assertEquals(3, counter.get());
        assertEquals(s, v1.valueOrVarSet());
        assertEquals(s, v2.valueOrVarSet());
        assertEquals(s, v3.valueOrVarSet());
    }

    @Test
    public void testBindToValueWithValue() throws Exception {
        Var v1 = new Var();
        v1.bindToValue(Int32.I32_0, null);
        assertEquals(Int32.I32_0, v1.valueOrVarSet());
    }

    @Test
    public void testBindToVarWithDifferentValues() {
        Var v1 = new Var(Int32.I32_0);
        assertEquals(Int32.I32_0, v1.valueOrVarSet());
        Var v2 = new Var(Int32.I32_1);
        assertEquals(Int32.I32_1, v2.valueOrVarSet());
        // Bind two Vars with different values--unification error
        Exception exc = assertThrows(UnificationError.class, () -> v1.bindToVar(v2, null));
        assertEquals("Unification error", exc.getMessage());
    }

    @Test
    public void testBindToVarWithLeftSideValue() throws Exception {
        Str s = Str.of("test value");
        Var v1 = new Var();
        s.bindToVar(v1, null);
        assertEquals(s, v1.valueOrVarSet());
    }

    @Test
    public void testBindToVarWithLeftSideValueAndRightSideEmptyVarSet() throws Exception {
        Str s = Str.of("test value");
        Var v1 = new Var(s);
        Var v2 = new Var();
        v1.bindToVar(v2, null);
        assertEquals(s, v1.valueOrVarSet());
        assertEquals(s, v2.valueOrVarSet());
    }

    @Test
    public void testBindToVarWithLeftSideVarAndRightSideVarSetOf2() throws Exception {
        Var v1 = new Var();
        Var v2 = new Var();
        Var v3 = new Var();
        v2.bindToVar(v3, null);
        assertSame(v2.valueOrVarSet(), v3.valueOrVarSet());
        assertNotEquals(v1.valueOrVarSet(), v2.valueOrVarSet());
        v1.bindToVar(v2, null);
        assertSame(v1.valueOrVarSet(), v2.valueOrVarSet());
        assertSame(v1.valueOrVarSet(), v3.valueOrVarSet());
        assertInstanceOf(VarSet.class, v1.valueOrVarSet());
        VarSet vs = (VarSet) v1.valueOrVarSet();
        assertEquals(3, vs.size());
        assertTrue(vs.contains(v1));
        assertTrue(vs.contains(v2));
        assertTrue(vs.contains(v3));
    }

    @Test
    public void testBindToVarWithLeftSideVarSetOf2AndRightSideVarSetOf2() throws Exception {
        Var v1 = new Var();
        Var v2 = new Var();
        Var v3 = new Var();
        Var v4 = new Var();
        v1.bindToVar(v2, null);
        v3.bindToVar(v4, null);
        assertSame(v1.valueOrVarSet(), v2.valueOrVarSet());
        assertSame(v3.valueOrVarSet(), v4.valueOrVarSet());
        assertNotEquals(v1.valueOrVarSet(), v3.valueOrVarSet());
        v1.bindToVar(v3, null);
        assertSame(v1.valueOrVarSet(), v2.valueOrVarSet());
        assertSame(v1.valueOrVarSet(), v3.valueOrVarSet());
        assertSame(v1.valueOrVarSet(), v4.valueOrVarSet());
        assertInstanceOf(VarSet.class, v1.valueOrVarSet());
        VarSet vs = (VarSet) v1.valueOrVarSet();
        assertEquals(4, vs.size());
        assertTrue(vs.contains(v1));
        assertTrue(vs.contains(v2));
        assertTrue(vs.contains(v3));
        assertTrue(vs.contains(v4));
    }

    @Test
    public void testBindToVarWithSameValue() throws Exception {
        Var v1 = new Var(Int32.I32_0);
        assertEquals(Int32.I32_0, v1.valueOrVarSet());
        Var v2 = new Var(Int32.I32_0);
        assertEquals(Int32.I32_0, v2.valueOrVarSet());
        // Bind two Vars with same value--validate unification
        v1.bindToVar(v2, null);
        assertEquals(Int32.I32_0, v1.valueOrVarSet());
        assertEquals(Int32.I32_0, v2.valueOrVarSet());
    }

    @Test
    public void testBindToVarWithValueDeduplication() throws Exception {
        Str s1 = Str.of("test value");
        Str s2 = Str.of("test value");
        Var v1 = new Var(s1);
        Var v2 = new Var(s2);
        v1.bindToVar(v2, null);
        assertTrue(v1.valueOrVarSet() == s1 && v2.valueOrVarSet() == s1 ||
            v1.valueOrVarSet() == s2 && v2.valueOrVarSet() == s2);
    }

    @Test
    public void testCheckComplete() {
        Var v1 = new Var();
        // At the Var level checkComplete() always throws a WaitVarException
        Exception exc = assertThrows(WaitVarException.class, v1::checkComplete);
        assertNull(exc.getMessage());
    }

    @Test
    public void testCreate() {
        Var v1 = new Var();
        assertEquals(EMPTY_VAR_SET, v1.valueOrVarSet());
        Var v2 = new Var(Int32.I32_0);
        assertEquals(Int32.I32_0, v2.valueOrVarSet());
    }

    @Test
    public void testEntailsValueOrIdentWithValue() throws Exception {
        Var v1 = new Var(Int32.I32_0);
        assertTrue(v1.entailsValueOrIdent(Int32.I32_0, Env.emptyEnv()));
        assertFalse(v1.entailsValueOrIdent(Int32.I32_1, Env.emptyEnv()));
    }

    @Test
    public void testEntailsValueOrVarWithDifferentVar() throws Exception {
        Var v1 = new Var(Int32.I32_0);
        Var v2 = new Var(Int32.I32_0);
        Var v3 = new Var(Int32.I32_1);
        assertTrue(v1.entailsValueOrVar(v2, null));
        assertFalse(v1.entailsValueOrVar(v3, null));
    }

    @Test
    public void testEntailsValueOrVarWithEmptyVarSets() {
        Var v1 = new Var();
        Var v2 = new Var();
        Exception exc = assertThrows(WaitVarException.class, () -> v1.entailsValueOrVar(v2, null));
        assertNull(exc.getMessage());
    }

    @Test
    public void testEntailsValueOrVarWithLeftSideValue() throws Exception {
        Var v1 = new Var(Int32.I32_0);
        assertTrue(Int32.I32_0.entailsValueOrVar(v1, null));
        assertFalse(Int32.I32_1.entailsValueOrVar(v1, null));
    }

    @Test
    public void testEntailsValueOrVarWithLeftSideValueAndRightSideVarSet() throws Exception {
        Var v1 = new Var(Int32.I32_0);
        Var v2 = new Var();
        Var v3 = new Var();
        v2.bindToVar(v3, null);
        Exception exc = assertThrows(WaitVarException.class, () -> v1.entailsValueOrVar(v2, null));
        assertNull(exc.getMessage());
    }

    @Test
    public void testEntailsValueOrVarWithLeftSideVarSetAndRightSideVarSet() throws Exception {
        Var v1 = new Var();
        Var v2 = new Var();
        Var v3 = new Var();
        Var v4 = new Var();
        v1.bindToVar(v2, null);
        v3.bindToVar(v4, null);
        Exception exc = assertThrows(WaitVarException.class, () -> v1.entailsValueOrVar(v3, null));
        assertNull(exc.getMessage());
    }

    @Test
    public void testEntailsValueOrVarWithRightSideValue() throws Exception {
        Var v1 = new Var(Int32.I32_0);
        assertTrue(v1.entailsValueOrVar(Int32.I32_0, null));
        assertFalse(v1.entailsValueOrVar(Int32.I32_1, null));
    }

    @Test
    public void testEntailsValueOrVarWithSameVar() throws Exception {
        Var v1 = new Var(Int32.I32_0);
        assertTrue(v1.entailsValueOrVar(v1, null));
    }

    @Test
    public void testResolveBoundVar() throws Exception {
        Var v1 = new Var(Int32.I32_0);
        assertEquals(Int32.I32_0, v1.resolveValue());
    }

    @Test
    public void testResolveUnboundVar() {
        Var v1 = new Var();
        Exception exc = assertThrows(WaitVarException.class, v1::resolveValue);
        assertNull(exc.getMessage());
    }

    @Test
    public void testToKernelString() {
        Var v1 = new Var();
        String toKernelString = v1.toKernelString();
        assertEquals("<<$var " + Integer.toHexString(System.identityHashCode(v1)) + ">>", toKernelString);
    }

    @Test
    public void testToString() {
        Var v1 = new Var();
        String toString = v1.toString();
        assertEquals("<<$var " + Integer.toHexString(System.identityHashCode(v1)) + ">>", toString);
    }

}
