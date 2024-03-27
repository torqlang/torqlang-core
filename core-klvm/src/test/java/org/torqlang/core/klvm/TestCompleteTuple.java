/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class TestCompleteTuple {

    @Test
    public void testAccessors() {

        CompleteTuple t1, t2;
        CompleteField f;

        Str a = Str.of("a");
        Str b = Str.of("b");

        t1 = CompleteTuple.create(List.of());
        assertNull(t1.findValue(Int32.I32_0));
        assertNull(t1.findValue(Str.of("not-a-feature")));

        t1 = CompleteTuple.create(List.of(a));
        f = t1.fieldAt(0);
        assertEquals(Int32.I32_0, f.feature);
        assertEquals(a, f.value);
        assertEquals(Int32.I32_0, t1.featureAt(0));
        assertEquals(a, t1.valueAt(0));
        assertEquals(a, t1.findValue(Int32.I32_0));
        assertEquals(a, t1.select(Int32.I32_0));
        assertNull(t1.findValue(Str.of("not-a-feature")));

        t2 = CompleteTuple.create(List.of(a));
        t1 = CompleteTuple.create(List.of(b, t2));
        f = t1.fieldAt(0);
        assertEquals(Int32.I32_0, f.feature);
        assertEquals(b, f.value);
        assertEquals(Int32.I32_0, t1.featureAt(0));
        assertEquals(b, t1.valueAt(0));
        assertEquals(b, t1.findValue(Int32.I32_0));
        assertEquals(b, t1.select(Int32.I32_0));
        f = t1.fieldAt(1);
        assertEquals(Int32.I32_1, f.feature);
        assertEquals(t2, f.value);
        assertEquals(Int32.I32_1, t1.featureAt(1));
        assertEquals(t2, t1.valueAt(1));
        assertEquals(t2, t1.findValue(Int32.I32_1));
        assertEquals(t2, t1.select(Int32.I32_1));
        assertNull(t1.findValue(Str.of("not-a-feature")));
    }

    @Test
    public void testAddAllTo() {

        CompleteTuple t1, t2;
        Collection<Object> c;

        Str a = Str.of("a");
        Str b = Str.of("b");

        t1 = CompleteTuple.create(List.of());
        c = new ArrayList<>();
        t1.addAllTo(c);
        assertEquals(0, c.size());

        t1 = CompleteTuple.create(List.of(a));
        c = new ArrayList<>();
        t1.addAllTo(c);
        assertEquals(1, c.size());
        assertEquals(a, c.iterator().next());

        t2 = CompleteTuple.create(List.of(a));
        t1 = CompleteTuple.create(List.of(b, t2));
        c = new ArrayList<>();
        t1.addAllTo(c);
        assertEquals(2, c.size());
        Iterator<Object> iter = c.iterator();
        Object next = iter.next();
        assertTrue(next.equals(b) || next.equals(t2));
        next = iter.next();
        assertTrue(next.equals(b) || next.equals(t2));
    }

    @Test
    public void testCompleteCircular() throws WaitVarException {

        //
        // Create two partial tuples that refer to each other
        //     p1 = [p2]
        //     p2 = [p1]
        //

        PartialTuple p1, p2;
        Var vx, vy, v1, v2;
        Complete cx, cy;

        v1 = new Var();
        p1 = PartialTuple.create(null, List.of(v1));
        vx = new Var(p1);

        v2 = new Var();
        p2 = PartialTuple.create(null, List.of(v2));
        vy = new Var(p2);

        v1.bindToValue(p2, null);
        v2.bindToValue(p1, null);

        //
        // Unify the two partial tuples, resulting in
        //     p1 = [p1]
        // or
        //     p2 = [p2]
        //

        vx.bindToVar(vy, null);

        assertTrue(vx.valueOrVarSet() == p1 || vx.valueOrVarSet() == p2);
        assertTrue(vy.valueOrVarSet() == p1 || vy.valueOrVarSet() == p2);
        assertSame(vx.valueOrVarSet(), vy.valueOrVarSet());

        //
        // Create two different complete tuples (because of checkComplete),
        // but from the same partial tuple
        //

        cx = vx.resolveValueOrVar().checkComplete();
        assertTrue(cx instanceof CompleteTuple);

        cy = vy.resolveValueOrVar().checkComplete();
        assertTrue(cy instanceof CompleteTuple);

        assertNotSame(cx, cy);
        assertTrue(cx.entails(cy, null));

        // Unify the two complete tuples, resulting in
        //     left = [left]
        // or
        //     right = [right]

        Var left = new Var(cx);
        Var right = new Var(cy);
        left.bindToVar(right, null);

        assertTrue(left.valueOrVarSet() == cx || left.valueOrVarSet() == cy);
        assertTrue(right.valueOrVarSet() == cx || right.valueOrVarSet() == cy);
        assertSame(left.valueOrVarSet(), right.valueOrVarSet());
    }

    @Test
    public void testCompleteSingle() throws Exception {

        PartialTuple p1;
        CompleteTuple t1;

        Str a = Str.of("a");

        p1 = PartialTuple.create(null, List.of(a));
        t1 = (CompleteTuple) p1.checkComplete();
        assertEquals(a, t1.findValue(Int32.I32_0));
    }

    @Test
    public void testCreate() {

        CompleteTuple t;
        Object nv;
        Map<?, ?> map;

        Str testLabel = Str.of("test-label");
        Str anotherTestLabel = Str.of("another-test-label");

        Str a = Str.of("a");
        Str b = Str.of("b");

        t = CompleteTuple.create(List.of());
        assertTrue(t.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_TUPLE, t.unificationPriority());
        assertEquals(0, t.fieldCount());
        assertEquals(Rec.DEFAULT_LABEL, t.label());
        nv = t.toNativeValue();
        assertTrue(nv instanceof List);
        assertEquals(List.of(), nv);

        t = CompleteTuple.create(testLabel, List.of());
        assertTrue(t.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_TUPLE, t.unificationPriority());
        assertEquals(0, t.fieldCount());
        assertEquals(testLabel, t.label());
        nv = t.toNativeValue();
        assertTrue(nv instanceof Map);
        map = (Map<?, ?>) nv;
        assertEquals(Map.of(Rec.NATIVE_LABEL, testLabel.value, Rec.NATIVE_TUPLE, List.of()), map);

        t = CompleteTuple.create(List.of(a));
        assertTrue(t.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_TUPLE, t.unificationPriority());
        assertEquals(1, t.fieldCount());
        assertEquals(Rec.DEFAULT_LABEL, t.label());
        assertEquals(a, t.select(Int32.I32_0));
        nv = t.toNativeValue();
        assertTrue(nv instanceof List);
        assertEquals(List.of("a"), nv);

        t = CompleteTuple.create(testLabel, List.of(a));
        assertTrue(t.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_TUPLE, t.unificationPriority());
        assertEquals(1, t.fieldCount());
        assertEquals(testLabel, t.label());
        assertEquals(a, t.select(Int32.I32_0));
        nv = t.toNativeValue();
        assertTrue(nv instanceof Map);
        map = (Map<?, ?>) nv;
        assertEquals(Map.of(Rec.NATIVE_LABEL, testLabel.value, Rec.NATIVE_TUPLE, List.of(a.value)), map);

        CompleteTuple t2 = CompleteTuple.create(List.of(a));
        assertTrue(t2.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_TUPLE, t2.unificationPriority());
        t = CompleteTuple.create(List.of(b, t2));
        assertTrue(t.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_TUPLE, t.unificationPriority());
        assertEquals(2, t.fieldCount());
        assertEquals(Rec.DEFAULT_LABEL, t.label());
        assertEquals(b, t.select(Int32.I32_0));
        assertEquals(t2, t.select(Int32.I32_1));
        nv = t.toNativeValue();
        assertTrue(nv instanceof List);
        assertEquals(List.of(b.value, List.of(a.value)), nv);

        t2 = CompleteTuple.create(testLabel, List.of(a));
        assertTrue(t2.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_TUPLE, t2.unificationPriority());
        t = CompleteTuple.create(anotherTestLabel, List.of(b, t2));
        assertTrue(t.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_TUPLE, t.unificationPriority());
        assertEquals(2, t.fieldCount());
        assertEquals(anotherTestLabel, t.label());
        assertEquals(b, t.select(Int32.I32_0));
        assertEquals(t2, t.select(Int32.I32_1));
        nv = t.toNativeValue();
        assertTrue(nv instanceof Map);
        Map<?, ?> innerNativeValue = Map.of(Rec.NATIVE_LABEL, testLabel.value, Rec.NATIVE_TUPLE, List.of(a.value));
        Map<?, ?> outerNativeValue = Map.of(Rec.NATIVE_LABEL, anotherTestLabel.value, Rec.NATIVE_TUPLE, List.of(b.value, innerNativeValue));
        assertEquals(outerNativeValue, nv);
    }

    @Test
    public void testFeatureNotFoundError0() {
        CompleteTuple t1;

        Exception exc;

        t1 = CompleteTuple.create(List.of());
        exc = assertThrows(FeatureNotFoundError.class, () -> t1.select(Int32.I32_0));
        assertEquals("Feature not found", exc.getMessage());
        exc = assertThrows(FeatureNotFoundError.class, () -> t1.select(Str.of("not-a-feature")));
        assertEquals("Feature not found", exc.getMessage());
    }

    @Test
    public void testFeatureNotFoundError1() {
        CompleteTuple t1;
        Str a = Str.of("a");

        Exception exc;

        t1 = CompleteTuple.create(List.of(a));
        exc = assertThrows(FeatureNotFoundError.class, () -> t1.select(Int32.I32_1));
        assertEquals("Feature not found", exc.getMessage());
        exc = assertThrows(FeatureNotFoundError.class, () -> t1.select(Str.of("not-a-feature")));
        assertEquals("Feature not found", exc.getMessage());
    }

    @Test
    public void testIndexException0() {
        CompleteTuple t1;

        Exception exc;

        t1 = CompleteTuple.create(List.of());
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> t1.fieldAt(0));
        assertEquals("Index 0 out of bounds for length 0", exc.getMessage());
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> t1.featureAt(0));
        assertEquals("Index 0 out of bounds for length 0", exc.getMessage());
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> t1.valueAt(0));
        assertEquals("Index 0 out of bounds for length 0", exc.getMessage());
        assertNull(t1.findValue(Int32.I32_0));
    }

    @Test
    public void testIndexException1() {
        CompleteTuple t1;
        Str a = Str.of("a");

        Exception exc;

        t1 = CompleteTuple.create(List.of(a));
        CompleteField f = t1.fieldAt(0);
        assertEquals(Int32.I32_0, f.feature);
        assertEquals(a, f.value);
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> t1.fieldAt(1));
        assertEquals("Index 1 out of bounds for length 1", exc.getMessage());
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> t1.featureAt(1));
        assertEquals("Index 1 out of bounds for length 1", exc.getMessage());
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> t1.valueAt(1));
        assertEquals("Index 1 out of bounds for length 1", exc.getMessage());
        assertEquals(a, t1.findValue(Int32.I32_0));
        assertNull(t1.findValue(Int32.I32_1));
    }

    @Test
    public void testToKernelString() {

        CompleteTuple t1;
        Str a = Str.of("a");
        Str b = Str.of("b");

        t1 = CompleteTuple.create(List.of());
        assertEquals("[]", t1.toKernelString());

        t1 = CompleteTuple.create(Str.of("my-label"), List.of());
        assertEquals("'my-label'#[]", t1.toKernelString());

        t1 = CompleteTuple.create(List.of(a));
        assertEquals("['a']", t1.toKernelString());

        t1 = CompleteTuple.create(Str.of("my-label"), List.of(a));
        assertEquals("'my-label'#['a']", t1.toKernelString());

        t1 = CompleteTuple.create(List.of(a, b));
        assertEquals("['a', 'b']", t1.toKernelString());

        t1 = CompleteTuple.create(Str.of("my-label"), List.of(a, b));
        assertEquals("'my-label'#['a', 'b']", t1.toKernelString());
    }

    @Test
    public void testToNativeValueCircularError() {

        CompleteTuple t;
        Exception exc;

        t = CompleteTuple.create(List.of());
        assertEquals(0, t.fieldCount());
        assertEquals(Rec.DEFAULT_LABEL, t.label());

        IdentityHashMap<CompleteRec, Object> memos = new IdentityHashMap<>();
        memos.put(t, Value.PRESENT);
        exc = assertThrows(IllegalArgumentException.class, () -> t.toNativeValue(memos));
        assertEquals("Circular reference error", exc.getMessage());
    }

    @Test
    public void testToString() {

        CompleteTuple t1;
        Str a = Str.of("a");
        Str b = Str.of("b");

        t1 = CompleteTuple.create(List.of());
        assertEquals("[]", t1.toString());

        t1 = CompleteTuple.create(Str.of("my-label"), List.of());
        assertEquals("'my-label'#[]", t1.toString());

        t1 = CompleteTuple.create(List.of(a));
        assertEquals("['a']", t1.toString());

        t1 = CompleteTuple.create(Str.of("my-label"), List.of(a));
        assertEquals("'my-label'#['a']", t1.toString());

        t1 = CompleteTuple.create(List.of(a, b));
        assertEquals("['a', 'b']", t1.toString());

        t1 = CompleteTuple.create(Str.of("my-label"), List.of(a, b));
        assertEquals("'my-label'#['a', 'b']", t1.toString());
    }

    @Test
    public void testUnify() throws WaitVarException {

        CompleteTuple t1, t3, t4;
        PartialTuple p2;

        Str a = Str.of("a");
        Str b = Str.of("b");

        //
        // Create two tuples, one complete and one partial. Their second elements are equal
        // but not the same.
        //     t1 = [0, [a, b], 1]
        //     p1 = [0, [a, b], 1]
        //

        t3 = CompleteTuple.create(List.of(a, b));
        t4 = CompleteTuple.create(List.of(a, b));
        t1 = CompleteTuple.create(List.of(Int32.I32_0, t3, Int32.I32_1));
        p2 = PartialTuple.create(null, List.of(Int32.I32_0, t4, Int32.I32_1));
        assertNotNull(t1.findValue(Int32.I32_1));
        assertNotNull(p2.findValue(Int32.I32_1));
        // Second elements are NOT same
        assertTrue(t1.findValue(Int32.I32_1).entails((Value) p2.findValue(Int32.I32_1), null));
        assertNotSame(t1.findValue(Int32.I32_1), p2.findValue(Int32.I32_1));

        //
        // Unify value t1 and p1 to deduplicate their fields
        //

        t1.bindToValue(p2, null);
        // Now, second elements ARE same
        assertSame(t1.findValue(Int32.I32_1), p2.findValue(Int32.I32_1));

        //
        // Unify vars referencing t1 and p1, resulting in just t1
        //

        Var v1 = new Var(t1);
        Var v2 = new Var(p2);
        // Var values are NOT same
        assertNotSame(v1.valueOrVarSet(), v2.valueOrVarSet());
        v1.bindToVar(v2, null);
        // Now, var values ARE same
        assertSame(v1.valueOrVarSet(), v2.valueOrVarSet());
        assertSame(t1, v1.valueOrVarSet());
    }

}
