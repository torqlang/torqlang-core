/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.Test;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TestCompleteRec {

    @Test
    public void testAccessors() {

        CompleteRec r1, r2;
        CompleteField f;

        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");

        Str a = Str.of("a");
        Str b = Str.of("b");

        r1 = CompleteRec.create(List.of());
        assertNull(r1.findValue(Str.of("not-a-feature")));

        r1 = CompleteRec.create(List.of(new CompleteField(zero, a)));
        f = r1.fieldAt(0);
        assertEquals(zero, f.feature);
        assertEquals(a, f.value);
        assertEquals(zero, r1.featureAt(0));
        assertEquals(a, r1.valueAt(0));
        assertEquals(a, r1.findValue(zero));
        assertEquals(a, r1.select(zero));
        assertNull(r1.findValue(Str.of("not-a-feature")));

        r2 = CompleteRec.create(List.of(new CompleteField(zero, a)));
        r1 = CompleteRec.create(List.of(new CompleteField(zero, b), new CompleteField(one, r2)));
        f = r1.fieldAt(0);
        assertEquals(zero, f.feature);
        assertEquals(b, f.value);
        assertEquals(zero, r1.featureAt(0));
        assertEquals(b, r1.valueAt(0));
        assertEquals(b, r1.findValue(zero));
        assertEquals(b, r1.select(zero));
        f = r1.fieldAt(1);
        assertEquals(one, f.feature);
        assertEquals(r2, f.value);
        assertEquals(one, r1.featureAt(1));
        assertEquals(r2, r1.valueAt(1));
        assertEquals(r2, r1.findValue(one));
        assertEquals(r2, r1.select(one));
        assertNull(r1.findValue(Str.of("not-a-feature")));
    }

    @Test
    public void testArity() {

        // Arity sort order: Int, Str, Bool (FALSE, TRUE), Eof, Nothing, Token

        CompleteRec r1;

        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");

        Str a = Str.of("a");

        r1 = CompleteRec.create(List.of(new CompleteField(zero, a), new CompleteField(Int32.I32_0, a)));
        assertEquals(Int32.I32_0, r1.featureAt(0));
        assertEquals(zero, r1.featureAt(1));

        r1 = CompleteRec.create(List.of(new CompleteField(one, a), new CompleteField(zero, a), new CompleteField(Int32.I32_0, a)));
        assertEquals(Int32.I32_0, r1.featureAt(0));
        assertEquals(zero, r1.featureAt(1));
        assertEquals(one, r1.featureAt(2));

        r1 = CompleteRec.create(List.of(new CompleteField(one, a), new CompleteField(zero, a), new CompleteField(Int32.I32_0, a)));
        assertEquals(Int32.I32_0, r1.featureAt(0));
        assertEquals(zero, r1.featureAt(1));
        assertEquals(one, r1.featureAt(2));

        r1 = CompleteRec.create(List.of(new CompleteField(Bool.FALSE, a), new CompleteField(one, a), new CompleteField(zero, a), new CompleteField(Int32.I32_0, a)));
        assertEquals(Int32.I32_0, r1.featureAt(0));
        assertEquals(zero, r1.featureAt(1));
        assertEquals(one, r1.featureAt(2));
        assertEquals(Bool.FALSE, r1.featureAt(3));

        r1 = CompleteRec.create(List.of(new CompleteField(Bool.TRUE, a), new CompleteField(Bool.FALSE, a), new CompleteField(one, a), new CompleteField(zero, a), new CompleteField(Int32.I32_0, a)));
        assertEquals(Int32.I32_0, r1.featureAt(0));
        assertEquals(zero, r1.featureAt(1));
        assertEquals(one, r1.featureAt(2));
        assertEquals(Bool.FALSE, r1.featureAt(3));
        assertEquals(Bool.TRUE, r1.featureAt(4));

        r1 = CompleteRec.create(List.of(new CompleteField(Eof.SINGLETON, a), new CompleteField(Bool.TRUE, a), new CompleteField(Bool.FALSE, a), new CompleteField(one, a), new CompleteField(zero, a), new CompleteField(Int32.I32_0, a)));
        assertEquals(Int32.I32_0, r1.featureAt(0));
        assertEquals(zero, r1.featureAt(1));
        assertEquals(one, r1.featureAt(2));
        assertEquals(Bool.FALSE, r1.featureAt(3));
        assertEquals(Bool.TRUE, r1.featureAt(4));
        assertEquals(Eof.SINGLETON, r1.featureAt(5));

        r1 = CompleteRec.create(List.of(new CompleteField(Nothing.SINGLETON, a), new CompleteField(Eof.SINGLETON, a), new CompleteField(Bool.TRUE, a), new CompleteField(Bool.FALSE, a), new CompleteField(one, a), new CompleteField(zero, a), new CompleteField(Int32.I32_0, a)));
        assertEquals(Int32.I32_0, r1.featureAt(0));
        assertEquals(zero, r1.featureAt(1));
        assertEquals(one, r1.featureAt(2));
        assertEquals(Bool.FALSE, r1.featureAt(3));
        assertEquals(Bool.TRUE, r1.featureAt(4));
        assertEquals(Eof.SINGLETON, r1.featureAt(5));
        assertEquals(Nothing.SINGLETON, r1.featureAt(6));

        final Token k1 = new Token();
        r1 = CompleteRec.create(List.of(new CompleteField(k1, a), new CompleteField(Nothing.SINGLETON, a), new CompleteField(Eof.SINGLETON, a), new CompleteField(Bool.TRUE, a), new CompleteField(Bool.FALSE, a), new CompleteField(one, a), new CompleteField(zero, a), new CompleteField(Int32.I32_0, a)));
        assertEquals(Int32.I32_0, r1.featureAt(0));
        assertEquals(zero, r1.featureAt(1));
        assertEquals(one, r1.featureAt(2));
        assertEquals(Bool.FALSE, r1.featureAt(3));
        assertEquals(Bool.TRUE, r1.featureAt(4));
        assertEquals(Eof.SINGLETON, r1.featureAt(5));
        assertEquals(Nothing.SINGLETON, r1.featureAt(6));
        assertEquals(k1, r1.featureAt(7));

        final Token k2 = new Token();
        r1 = CompleteRec.create(List.of(new CompleteField(k2, a), new CompleteField(k1, a), new CompleteField(Nothing.SINGLETON, a), new CompleteField(Eof.SINGLETON, a), new CompleteField(Bool.TRUE, a), new CompleteField(Bool.FALSE, a), new CompleteField(one, a), new CompleteField(zero, a), new CompleteField(Int32.I32_0, a)));
        assertEquals(Int32.I32_0, r1.featureAt(0));
        assertEquals(zero, r1.featureAt(1));
        assertEquals(one, r1.featureAt(2));
        assertEquals(Bool.FALSE, r1.featureAt(3));
        assertEquals(Bool.TRUE, r1.featureAt(4));
        assertEquals(Eof.SINGLETON, r1.featureAt(5));
        assertEquals(Nothing.SINGLETON, r1.featureAt(6));
        assertEquals(k1, r1.featureAt(7));
        assertEquals(k2, r1.featureAt(8));
    }

    @Test
    public void testCompleteCircular() throws WaitVarException {

        Str zero = Str.of("0-zero");

        //
        // Create two partial tuples that refer to each other
        //     p1 = {zero: p2}
        //     p2 = {zero: p1}
        //

        PartialRec p1, p2;
        Var vx, vy, v1, v2;
        Complete cx, cy;

        v1 = new Var();
        p1 = PartialRec.create(null, List.of(), List.of(new PartialField(zero, v1)));
        vx = new Var(p1);

        v2 = new Var();
        p2 = PartialRec.create(null, List.of(), List.of(new PartialField(zero, v2)));
        vy = new Var(p2);

        v1.bindToValue(p2, null);
        v2.bindToValue(p1, null);

        //
        // Unify the two partial tuples, resulting in
        //     p1 = {zero: p1}
        // or
        //     p2 = {zero: p2}
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
        assertTrue(cx instanceof CompleteRec);

        cy = vy.resolveValueOrVar().checkComplete();
        assertTrue(cy instanceof CompleteRec);

        assertNotSame(cx, cy);
        assertTrue(cx.entails(cy, null));

        // Unify the two complete tuples, resulting in
        //     left = {zero: left}
        // or
        //     right = {zero: right}

        Var left = new Var(cx);
        Var right = new Var(cy);
        left.bindToVar(right, null);

        assertTrue(left.valueOrVarSet() == cx || left.valueOrVarSet() == cy);
        assertTrue(right.valueOrVarSet() == cx || right.valueOrVarSet() == cy);
        assertSame(left.valueOrVarSet(), right.valueOrVarSet());
    }

    @Test
    public void testCompleteSingle() throws Exception {

        PartialRec p1;
        CompleteRec r1;

        Str zero = Str.of("0-zero");
        Str a = Str.of("a");

        p1 = PartialRec.create(null, List.of(), List.of(new PartialField(zero, a)));
        r1 = (CompleteRec) p1.checkComplete();
        assertEquals(a, r1.findValue(zero));
    }

    @Test
    public void testCreate() {

        CompleteRec r;
        Object nv;
        Map<?, ?> map;

        Str testLabel = Str.of("test-label");
        Str anotherTestLabel = Str.of("another-test-label");

        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");

        Str a = Str.of("a");
        Str b = Str.of("b");

        r = CompleteRec.create(List.of());
        assertTrue(r.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_REC, r.unificationPriority());
        assertEquals(0, r.fieldCount());
        assertEquals(Rec.DEFAULT_LABEL, r.label());
        nv = r.toNativeValue();
        assertTrue(nv instanceof Map);
        assertEquals(Map.of(), nv);

        r = CompleteRec.create(testLabel, List.of());
        assertTrue(r.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_REC, r.unificationPriority());
        assertEquals(0, r.fieldCount());
        assertEquals(testLabel, r.label());
        nv = r.toNativeValue();
        assertTrue(nv instanceof Map);
        map = (Map<?, ?>) nv;
        assertEquals(Map.of(Rec.$LABEL, testLabel.value, Rec.$REC, Map.of()), map);

        r = CompleteRec.create(null, List.of(new CompleteField(zero, a)));
        assertTrue(r.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_REC, r.unificationPriority());
        assertEquals(1, r.fieldCount());
        assertEquals(Rec.DEFAULT_LABEL, r.label());
        assertEquals(a, r.select(zero));
        nv = r.toNativeValue();
        assertTrue(nv instanceof Map);
        assertEquals(Map.of(zero.value, a.value), nv);

        r = CompleteRec.create(testLabel, List.of(new CompleteField(zero, a)));
        assertTrue(r.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_REC, r.unificationPriority());
        assertEquals(1, r.fieldCount());
        assertEquals(testLabel, r.label());
        assertEquals(a, r.select(zero));
        nv = r.toNativeValue();
        assertTrue(nv instanceof Map);
        map = (Map<?, ?>) nv;
        assertEquals(Map.of(Rec.$LABEL, testLabel.value, Rec.$REC, Map.of(zero.value, a.value)), map);

        CompleteRec r2 = CompleteRec.create(List.of(new CompleteField(zero, a)));
        assertTrue(r2.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_REC, r2.unificationPriority());
        r = CompleteRec.create(null, List.of(new CompleteField(zero, b), new CompleteField(one, r2)));
        assertTrue(r.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_REC, r.unificationPriority());
        assertEquals(2, r.fieldCount());
        assertEquals(Rec.DEFAULT_LABEL, r.label());
        assertEquals(b, r.select(zero));
        assertEquals(r2, r.select(one));
        nv = r.toNativeValue();
        assertTrue(nv instanceof Map);
        assertEquals(Map.of(zero.value, b.value, one.value, Map.of(zero.value, a.value)), nv);

        r2 = CompleteRec.create(testLabel, List.of(new CompleteField(zero, a)));
        assertTrue(r2.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_REC, r2.unificationPriority());
        r = CompleteRec.create(anotherTestLabel, List.of(new CompleteField(zero, b), new CompleteField(one, r2)));
        assertTrue(r.isValidKey());
        assertEquals(UnificationPriority.COMPLETE_REC, r.unificationPriority());
        assertEquals(2, r.fieldCount());
        assertEquals(anotherTestLabel, r.label());
        assertEquals(b, r.select(zero));
        assertEquals(r2, r.select(one));
        nv = r.toNativeValue();
        assertTrue(nv instanceof Map);
        Map<?, ?> innerNativeValue = Map.of(Rec.$LABEL, testLabel.value, Rec.$REC, Map.of(zero.value, a.value));
        Map<?, ?> outerNativeValue = Map.of(Rec.$LABEL, anotherTestLabel.value, Rec.$REC, Map.of(zero.value, b.value, one.value, innerNativeValue));
        assertEquals(outerNativeValue, nv);
    }

    @Test
    public void testDuplicateFeatureError() {

        Exception exc;

        Str zero = Str.of("0-zero");

        Str a = Str.of("a");
        Str b = Str.of("b");

        exc = assertThrows(DuplicateFeatureError.class,
            () -> CompleteRec.create(List.of(new CompleteField(zero, a), new CompleteField(zero, b)))
        );
        assertEquals("Duplicate feature", exc.getMessage());
    }

    @Test
    public void testFeatureNotFoundError0() {
        CompleteRec r1;

        Exception exc;

        r1 = CompleteRec.create(List.of());
        exc = assertThrows(FeatureNotFoundError.class, () -> r1.select(Int32.I32_0));
        assertEquals("Feature not found", exc.getMessage());
        exc = assertThrows(FeatureNotFoundError.class, () -> r1.select(Str.of("not-a-feature")));
        assertEquals("Feature not found", exc.getMessage());
    }

    @Test
    public void testFeatureNotFoundError1() {

        CompleteRec r1;

        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");

        Str a = Str.of("a");

        Exception exc;

        r1 = CompleteRec.create(List.of(new CompleteField(zero, a)));
        exc = assertThrows(FeatureNotFoundError.class, () -> r1.select(one));
        assertEquals("Feature not found", exc.getMessage());
        exc = assertThrows(FeatureNotFoundError.class, () -> r1.select(Int32.I32_2));
        assertEquals("Feature not found", exc.getMessage());
    }

    @Test
    public void testIndexException0() {

        CompleteRec r1;

        Exception exc;

        r1 = CompleteRec.create(List.of());
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> r1.fieldAt(0));
        assertEquals("Index 0 out of bounds for length 0", exc.getMessage());
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> r1.featureAt(0));
        assertEquals("Index 0 out of bounds for length 0", exc.getMessage());
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> r1.valueAt(0));
        assertEquals("Index 0 out of bounds for length 0", exc.getMessage());
        assertNull(r1.findValue(Int32.I32_0));
    }

    @Test
    public void testIndexException1() {

        CompleteRec r1;

        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");

        Str a = Str.of("a");

        Exception exc;

        r1 = CompleteRec.create(List.of(new CompleteField(zero, a)));
        CompleteField f = r1.fieldAt(0);
        assertEquals(zero, f.feature);
        assertEquals(a, f.value);
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> r1.fieldAt(1));
        assertEquals("Index 1 out of bounds for length 1", exc.getMessage());
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> r1.featureAt(1));
        assertEquals("Index 1 out of bounds for length 1", exc.getMessage());
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> r1.valueAt(1));
        assertEquals("Index 1 out of bounds for length 1", exc.getMessage());
        assertEquals(a, r1.findValue(zero));
        assertNull(r1.findValue(one));
    }

    @Test
    public void testToKernelString() {

        CompleteRec r1;

        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");

        Str a = Str.of("a");
        Str b = Str.of("b");

        r1 = CompleteRec.create(List.of());
        assertEquals("{}", r1.toKernelString());

        r1 = CompleteRec.create(Str.of("my-label"), List.of());
        assertEquals("'my-label'#{}", r1.toKernelString());

        r1 = CompleteRec.create(List.of(new CompleteField(zero, a)));
        assertEquals("{'0-zero': 'a'}", r1.toKernelString());

        r1 = CompleteRec.create(Str.of("my-label"), List.of(new CompleteField(zero, a)));
        assertEquals("'my-label'#{'0-zero': 'a'}", r1.toKernelString());

        r1 = CompleteRec.create(List.of(new CompleteField(zero, a), new CompleteField(one, b)));
        assertEquals("{'0-zero': 'a', '1-one': 'b'}", r1.toKernelString());

        r1 = CompleteRec.create(Str.of("my-label"), List.of(new CompleteField(zero, a), new CompleteField(one, b)));
        assertEquals("'my-label'#{'0-zero': 'a', '1-one': 'b'}", r1.toKernelString());
    }

    @Test
    public void testToNativeValueCircularError() {

        CompleteRec r;
        Exception exc;

        r = CompleteRec.create(List.of());
        assertEquals(0, r.fieldCount());
        assertEquals(Rec.DEFAULT_LABEL, r.label());

        IdentityHashMap<CompleteRec, Object> memos = new IdentityHashMap<>();
        memos.put(r, Value.PRESENT);
        exc = assertThrows(IllegalArgumentException.class, () -> r.toNativeValue(memos));
        assertEquals("Circular reference error", exc.getMessage());
    }

    @Test
    public void testToString() {

        CompleteRec r1;

        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");

        Str a = Str.of("a");
        Str b = Str.of("b");

        r1 = CompleteRec.create(List.of());
        assertEquals("{}", r1.toString());

        r1 = CompleteRec.create(Str.of("my-label"), List.of());
        assertEquals("'my-label'#{}", r1.toString());

        r1 = CompleteRec.create(List.of(new CompleteField(zero, a)));
        assertEquals("{'0-zero': 'a'}", r1.toString());

        r1 = CompleteRec.create(Str.of("my-label"), List.of(new CompleteField(zero, a)));
        assertEquals("'my-label'#{'0-zero': 'a'}", r1.toString());

        r1 = CompleteRec.create(List.of(new CompleteField(zero, a), new CompleteField(one, b)));
        assertEquals("{'0-zero': 'a', '1-one': 'b'}", r1.toString());

        r1 = CompleteRec.create(Str.of("my-label"), List.of(new CompleteField(zero, a), new CompleteField(one, b)));
        assertEquals("'my-label'#{'0-zero': 'a', '1-one': 'b'}", r1.toString());
    }

    @Test
    public void testUnify() throws WaitVarException {

        CompleteRec r1, r3, r4;
        PartialRec p2;

        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");
        Str two = Str.of("2-two");

        Str a = Str.of("a");
        Str b = Str.of("b");

        Str x = Str.of("x");
        Str y = Str.of("y");

        //
        // Create two tuples, one complete and one partial. Their second elements are equal
        // but not the same.
        //     r1 = {'0-zero': x, '1-one': {'0-zero': a, '1-one': b}, '2-two': y}
        //     p1 = {'0-zero': x, '1-one': {'0-zero': a, '1-one': b}, '2-two': y}
        //

        r3 = CompleteRec.create(List.of(new CompleteField(zero, a), new CompleteField(one, b)));
        r4 = CompleteRec.create(List.of(new CompleteField(zero, a), new CompleteField(one, b)));
        r1 = CompleteRec.create(List.of(new CompleteField(zero, x), new CompleteField(one, r3), new CompleteField(two, y)));
        p2 = PartialRec.create(null, List.of(), List.of(new PartialField(zero, x), new PartialField(one, r4), new PartialField(two, y)));
        assertNotNull(r1.findValue(one));
        assertNotNull(p2.findValue(one));
        // Second elements are NOT same
        assertTrue(r1.findValue(one).entails((Value) p2.findValue(one), null));
        assertNotSame(r1.findValue(one), p2.findValue(one));

        //
        // Unify value r1 and p1 to deduplicate their fields
        //

        r1.bindToValue(p2, null);
        // Now, second elements ARE same
        assertSame(r1.findValue(one), p2.findValue(one));

        //
        // Unify vars referencing r1 and p1, resulting in just r1
        //

        Var v1 = new Var(r1);
        Var v2 = new Var(p2);
        // Var values are NOT same
        assertNotSame(v1.valueOrVarSet(), v2.valueOrVarSet());
        v1.bindToVar(v2, null);
        // Now, var values ARE same
        assertSame(v1.valueOrVarSet(), v2.valueOrVarSet());
        assertSame(r1, v1.valueOrVarSet());
    }

}
