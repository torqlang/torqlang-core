/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestPartialRec {

    @Test
    public void testCreateWithComplete1() throws WaitException {

        PartialRec r;
        Field f;
        Complete c;
        CompleteRec cr;

        Str a = Str.of("a");

        Str zero = Str.of("0-zero");

        r = PartialRec.create(null, List.of(), List.of(new PartialField(zero, a)));
        r.checkDetermined();
        assertFalse(r.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_REC, r.unificationPriority());
        assertNull(r.futureLabel());
        assertEquals(Rec.DEFAULT_LABEL, r.label());
        assertEquals(0, r.futureFieldCount());
        assertEquals(1, r.fieldCount());
        assertEquals(1, r.totalFieldCount());
        f = r.fieldAt(0);
        assertEquals(zero, f.feature().resolveValue());
        assertEquals(a, f.value().resolveValue());
        assertEquals(zero, r.featureAt(0));
        assertEquals(a, r.valueAt(0));
        assertEquals(a, r.findValue(zero));
        assertEquals(a, r.select(zero));
        c = r.checkComplete();
        assertInstanceOf(CompleteRec.class, c);
        cr = (CompleteRec) c;
        assertEquals(Rec.DEFAULT_LABEL, cr.label());
        assertEquals(1, cr.fieldCount());
        f = cr.fieldAt(0);
        assertEquals(zero, f.feature().resolveValue());
        assertEquals(a, f.value().resolveValue());
        assertEquals(zero, cr.featureAt(0));
        assertEquals(a, cr.valueAt(0));
        assertEquals(a, cr.findValue(zero));
        assertEquals(a, cr.select(zero));
    }

    @Test
    public void testCreateWithComplete1WithLabel() throws WaitException {

        PartialRec r;
        Field f;
        Complete c;
        CompleteRec cr;

        Str testLabel = Str.of("test-label");

        Str a = Str.of("a");

        Str zero = Str.of("0-zero");

        r = PartialRec.create(testLabel, List.of(), List.of(new PartialField(zero, a)));
        r.checkDetermined();
        assertFalse(r.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_REC, r.unificationPriority());
        assertNull(r.futureLabel());
        assertEquals(testLabel, r.label());
        assertEquals(0, r.futureFieldCount());
        assertEquals(1, r.fieldCount());
        assertEquals(1, r.totalFieldCount());
        f = r.fieldAt(0);
        assertEquals(zero, f.feature().resolveValue());
        assertEquals(a, f.value().resolveValue());
        assertEquals(zero, r.featureAt(0));
        assertEquals(a, r.valueAt(0));
        assertEquals(a, r.findValue(zero));
        assertEquals(a, r.select(zero));
        c = r.checkComplete();
        assertInstanceOf(CompleteRec.class, c);
        cr = (CompleteRec) c;
        assertEquals(testLabel, cr.label());
        assertEquals(1, cr.fieldCount());
        f = cr.fieldAt(0);
        assertEquals(zero, f.feature().resolveValue());
        assertEquals(a, f.value().resolveValue());
        assertEquals(zero, cr.featureAt(0));
        assertEquals(a, cr.valueAt(0));
        assertEquals(a, cr.findValue(zero));
        assertEquals(a, cr.select(zero));
    }

    @Test
    public void testCreateWithComplete2() throws WaitException {

        PartialRec r;
        Field f;
        Complete c;
        CompleteRec cr;

        Str a = Str.of("a");
        Str b = Str.of("b");

        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");

        r = PartialRec.create(null, List.of(), List.of(new PartialField(zero, a), new PartialField(one, b)));
        r.checkDetermined();
        assertFalse(r.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_REC, r.unificationPriority());
        assertNull(r.futureLabel());
        assertEquals(Rec.DEFAULT_LABEL, r.label());
        f = r.fieldAt(0);
        assertEquals(zero, f.feature().resolveValue());
        assertEquals(a, f.value().resolveValue());
        assertEquals(zero, r.featureAt(0));
        assertEquals(a, r.valueAt(0));
        assertEquals(a, r.findValue(zero));
        assertEquals(a, r.select(zero));
        f = r.fieldAt(1);
        assertEquals(one, f.feature().resolveValue());
        assertEquals(b, f.value().resolveValue());
        assertEquals(one, r.featureAt(1));
        assertEquals(b, r.valueAt(1));
        assertEquals(b, r.findValue(one));
        assertEquals(b, r.select(one));
        c = r.checkComplete();
        assertInstanceOf(CompleteRec.class, c);
        cr = (CompleteRec) c;
        f = cr.fieldAt(0);
        assertEquals(zero, f.feature().resolveValue());
        assertEquals(a, f.value().resolveValue());
        assertEquals(zero, cr.featureAt(0));
        assertEquals(a, cr.valueAt(0));
        assertEquals(a, cr.findValue(zero));
        assertEquals(a, cr.select(zero));
        f = cr.fieldAt(1);
        assertEquals(one, f.feature().resolveValue());
        assertEquals(b, f.value().resolveValue());
        assertEquals(one, cr.featureAt(1));
        assertEquals(b, cr.valueAt(1));
        assertEquals(b, cr.findValue(one));
        assertEquals(b, cr.select(one));
    }

    @Test
    public void testCreateWithComplete2WithLabel() throws WaitException {

        PartialRec r;
        Field f;
        Complete c;
        CompleteRec cr;

        Str testLabel = Str.of("test-label");

        Str a = Str.of("a");
        Str b = Str.of("b");

        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");

        r = PartialRec.create(testLabel, List.of(), List.of(new PartialField(zero, a), new PartialField(one, b)));
        r.checkDetermined();
        assertFalse(r.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_REC, r.unificationPriority());
        assertNull(r.futureLabel());
        assertEquals(testLabel, r.label());
        f = r.fieldAt(0);
        assertEquals(zero, f.feature().resolveValue());
        assertEquals(a, f.value().resolveValue());
        assertEquals(zero, r.featureAt(0));
        assertEquals(a, r.valueAt(0));
        assertEquals(a, r.findValue(zero));
        assertEquals(a, r.select(zero));
        f = r.fieldAt(1);
        assertEquals(one, f.feature().resolveValue());
        assertEquals(b, f.value().resolveValue());
        assertEquals(one, r.featureAt(1));
        assertEquals(b, r.valueAt(1));
        assertEquals(b, r.findValue(one));
        assertEquals(b, r.select(one));
        c = r.checkComplete();
        assertInstanceOf(CompleteRec.class, c);
        cr = (CompleteRec) c;
        f = cr.fieldAt(0);
        assertEquals(zero, f.feature().resolveValue());
        assertEquals(a, f.value().resolveValue());
        assertEquals(zero, cr.featureAt(0));
        assertEquals(a, cr.valueAt(0));
        assertEquals(a, cr.findValue(zero));
        assertEquals(a, cr.select(zero));
        f = cr.fieldAt(1);
        assertEquals(one, f.feature().resolveValue());
        assertEquals(b, f.value().resolveValue());
        assertEquals(one, cr.featureAt(1));
        assertEquals(b, cr.valueAt(1));
        assertEquals(b, cr.findValue(one));
        assertEquals(b, cr.select(one));
    }

    @Test
    public void testCreateWithEmpty() throws WaitException {

        PartialRec r;

        Str zero = Str.of("0-zero");

        r = PartialRec.create(null, List.of(), List.of());
        r.checkDetermined();
        assertFalse(r.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_REC, r.unificationPriority());
        assertNull(r.futureLabel());
        assertEquals(Rec.DEFAULT_LABEL, r.label());
        assertEquals(0, r.futureFieldCount());
        assertEquals(0, r.fieldCount());
        assertEquals(0, r.totalFieldCount());
        assertNull(r.findValue(zero));
    }

    @Test
    public void testCreateWithEmptyWithFutureLabel() throws WaitVarException {

        PartialRec r;

        Var labelVar = new Var();
        Str testLabel = Str.of("test-label");

        r = PartialRec.create(labelVar, List.of(), List.of());
        Collection<Var> unboundVars = r.sweepUndeterminedVars();
        assertEquals(1, unboundVars.size());
        assertEquals(labelVar, unboundVars.iterator().next());
        {
            Exception exc = assertThrows(WaitVarException.class, r::checkDetermined);
            assertNull(exc.getMessage());
        }

        labelVar.bindToValue(testLabel, null);
        unboundVars = r.sweepUndeterminedVars();
        assertEquals(0, unboundVars.size());
        r.checkDetermined();
        assertFalse(r.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_REC, r.unificationPriority());
        assertEquals(testLabel, r.label());
    }

    @Test
    public void testCreateWithEmptyWithLabel() throws WaitException {

        PartialRec r;

        Str testLabel = Str.of("test-label");

        Str zero = Str.of("0-zero");

        r = PartialRec.create(testLabel, List.of(), List.of());
        r.checkDetermined();
        assertFalse(r.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_REC, r.unificationPriority());
        assertNull(r.futureLabel());
        assertEquals(testLabel, r.label());
        assertEquals(0, r.futureFieldCount());
        assertEquals(0, r.fieldCount());
        assertEquals(0, r.totalFieldCount());
        assertNull(r.findValue(zero));
    }

    @Test
    public void testCreateWithFuture1() throws WaitVarException {

        PartialRec r;

        Var featureVar = new Var();
        Str featureZero = Str.of("0-zero");

        Str a = Str.of("a");

        r = PartialRec.create(null, List.of(new FutureField(featureVar, a)), List.of());
        Collection<Var> unboundVars = r.sweepUndeterminedVars();
        assertEquals(1, unboundVars.size());
        assertEquals(featureVar, unboundVars.iterator().next());
        {
            Exception exc = assertThrows(WaitVarException.class, r::checkDetermined);
            assertNull(exc.getMessage());
        }

        featureVar.bindToValue(featureZero, null);
        unboundVars = r.sweepUndeterminedVars();
        assertEquals(0, unboundVars.size());
        r.checkDetermined();
        assertFalse(r.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_REC, r.unificationPriority());
        assertEquals(featureZero, r.featureAt(0));
    }

    @Test
    public void testCreateWithPartial1() throws WaitException {

        PartialRec r;
        Field f;
        Complete c;
        CompleteRec cr;

        Str zero = Str.of("0-zero");

        Var aVar = new Var();
        Str a = Str.of("a");

        Exception exc;

        r = PartialRec.create(null, List.of(), List.of(new PartialField(zero, aVar)));
        r.checkDetermined();
        assertFalse(r.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_REC, r.unificationPriority());
        assertNull(r.futureLabel());
        assertEquals(Rec.DEFAULT_LABEL, r.label());
        assertEquals(0, r.futureFieldCount());
        assertEquals(1, r.fieldCount());
        assertEquals(1, r.totalFieldCount());
        f = r.fieldAt(0);
        assertEquals(zero, f.feature().resolveValue());
        {
            final Field f1 = f;
            exc = assertThrows(WaitVarException.class, () -> f1.value().resolveValue());
            assertNull(exc.getMessage());
        }
        assertEquals(zero, r.featureAt(0));
        assertEquals(aVar, r.valueAt(0));
        assertEquals(aVar, r.findValue(zero));
        assertEquals(aVar, r.select(zero));
        exc = assertThrows(WaitVarException.class, r::checkComplete);
        assertNull(exc.getMessage());

        aVar.bindToValue(a, null);
        c = r.checkComplete();
        assertInstanceOf(CompleteRec.class, c);
        cr = (CompleteRec) c;
        assertEquals(Rec.DEFAULT_LABEL, cr.label());
        assertEquals(1, cr.fieldCount());
        f = cr.fieldAt(0);
        assertEquals(zero, f.feature().resolveValue());
        assertEquals(a, f.value().resolveValue());
        assertEquals(zero, cr.featureAt(0));
        assertEquals(a, cr.valueAt(0));
        assertEquals(a, cr.findValue(zero));
        assertEquals(a, cr.select(zero));
    }

    @Test
    public void testDuplicateFeatureError() {

        Exception exc;

        Str zero = Str.of("0-zero");

        Str a = Str.of("a");
        Str b = Str.of("b");

        exc = assertThrows(DuplicateFeatureError.class,
            () -> PartialRec.create(null, List.of(), List.of(new PartialField(zero, a), new PartialField(zero, b)))
        );
        assertEquals("Duplicate feature", exc.getMessage());
    }

    @Test
    public void testFeatureNotFoundError0() {
        PartialRec r1;

        Exception exc;

        r1 = PartialRec.create(null, List.of(), List.of());
        exc = assertThrows(FeatureNotFoundError.class, () -> r1.select(Int32.I32_0));
        assertEquals("Feature not found", exc.getMessage());
        exc = assertThrows(FeatureNotFoundError.class, () -> r1.select(Str.of("not-a-feature")));
        assertEquals("Feature not found", exc.getMessage());
    }

    @Test
    public void testFeatureNotFoundError1() {

        PartialRec r1;

        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");

        Str a = Str.of("a");

        Exception exc;

        r1 = PartialRec.create(null, List.of(), List.of(new PartialField(zero, a)));
        exc = assertThrows(FeatureNotFoundError.class, () -> r1.select(one));
        assertEquals("Feature not found", exc.getMessage());
        exc = assertThrows(FeatureNotFoundError.class, () -> r1.select(Int32.I32_2));
        assertEquals("Feature not found", exc.getMessage());
    }

    @Test
    public void testIndexException0() {

        Exception exc;

        PartialRec r = PartialRec.create(null, List.of(), List.of());
        exc = assertThrows(IndexOutOfBoundsException.class, () -> r.fieldAt(0));
        assertEquals("Index 0 out of bounds for length 0", exc.getMessage());
    }

    @Test
    public void testIndexExceptionWithFuture1() {

        Str a = Str.of("a");

        Exception exc;

        // Fields are not yet determined, so length will be 0

        PartialRec r = PartialRec.create(null, List.of(new FutureField(new Var(), a)), List.of());
        exc = assertThrows(IndexOutOfBoundsException.class, () -> r.fieldAt(0));
        assertEquals("Index 0 out of bounds for length 0", exc.getMessage());
    }

    @Test
    public void testIndexExceptionWithPartial1() {

        Str a = Str.of("a");

        Str zero = Str.of("0-zero");

        Exception exc;

        // Fields are determined, so length will be 1

        PartialRec r = PartialRec.create(null, List.of(), List.of(new PartialField(zero, a)));
        assertNotNull(r.fieldAt(0));
        exc = assertThrows(IndexOutOfBoundsException.class, () -> r.fieldAt(1));
        assertEquals("Index 1 out of bounds for length 1", exc.getMessage());
    }

    @Test
    public void testToKernelString() {

        PartialRec r1;

        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");

        Str a = Str.of("a");
        Str b = Str.of("b");

        r1 = PartialRec.create(null, List.of(), List.of());
        assertEquals("{}", r1.toKernelString());

        r1 = PartialRec.create(Str.of("my-label"), List.of(), List.of());
        assertEquals("'my-label'#{}", r1.toKernelString());

        r1 = PartialRec.create(null, List.of(), List.of(new PartialField(zero, a)));
        assertEquals("{'0-zero': 'a'}", r1.toKernelString());

        r1 = PartialRec.create(Str.of("my-label"), List.of(), List.of(new PartialField(zero, a)));
        assertEquals("'my-label'#{'0-zero': 'a'}", r1.toKernelString());

        r1 = PartialRec.create(null, List.of(), List.of(new PartialField(zero, a), new PartialField(one, b)));
        assertEquals("{'0-zero': 'a', '1-one': 'b'}", r1.toKernelString());

        r1 = PartialRec.create(Str.of("my-label"), List.of(), List.of(new PartialField(zero, a), new PartialField(one, b)));
        assertEquals("'my-label'#{'0-zero': 'a', '1-one': 'b'}", r1.toKernelString());
    }

    @Test
    public void testToString() {

        PartialRec r1;

        Str zero = Str.of("0-zero");
        Str one = Str.of("1-one");

        Str a = Str.of("a");
        Str b = Str.of("b");

        r1 = PartialRec.create(null, List.of(), List.of());
        assertEquals("{}", r1.toString());

        r1 = PartialRec.create(Str.of("my-label"), List.of(), List.of());
        assertEquals("'my-label'#{}", r1.toString());

        r1 = PartialRec.create(null, List.of(), List.of(new PartialField(zero, a)));
        assertEquals("{'0-zero': 'a'}", r1.toString());

        r1 = PartialRec.create(Str.of("my-label"), List.of(), List.of(new PartialField(zero, a)));
        assertEquals("'my-label'#{'0-zero': 'a'}", r1.toString());

        r1 = PartialRec.create(null, List.of(), List.of(new PartialField(zero, a), new PartialField(one, b)));
        assertEquals("{'0-zero': 'a', '1-one': 'b'}", r1.toString());

        r1 = PartialRec.create(Str.of("my-label"), List.of(), List.of(new PartialField(zero, a), new PartialField(one, b)));
        assertEquals("'my-label'#{'0-zero': 'a', '1-one': 'b'}", r1.toString());
    }

}
