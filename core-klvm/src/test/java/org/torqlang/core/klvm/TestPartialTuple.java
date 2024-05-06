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

public class TestPartialTuple {

    @Test
    public void testCreateWithComplete1() throws WaitException {

        PartialTuple t;
        Str a = Str.of("a");

        t = PartialTuple.create(null, List.of(a));
        t.checkDetermined();
        assertFalse(t.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_TUPLE, t.unificationPriority());
        assertNull(t.futureLabel());
        assertEquals(Rec.DEFAULT_LABEL, t.label());

        assertEquals(0, t.futureFieldCount());
        assertEquals(1, t.fieldCount());
        assertEquals(1, t.totalFieldCount());
        assertEquals(a, t.findValue(Int32.I32_0));
    }

    @Test
    public void testCreateWithComplete2() throws WaitException {

        PartialTuple t;
        Str a = Str.of("a");
        Str b = Str.of("b");

        t = PartialTuple.create(null, List.of(a, b));
        t.checkDetermined();
        assertFalse(t.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_TUPLE, t.unificationPriority());
        assertNull(t.futureLabel());
        assertEquals(Rec.DEFAULT_LABEL, t.label());

        assertEquals(0, t.futureFieldCount());
        assertEquals(2, t.fieldCount());
        assertEquals(2, t.totalFieldCount());
        assertEquals(a, t.findValue(Int32.I32_0));
        assertEquals(b, t.findValue(Int32.I32_1));
    }

    @Test
    public void testCreateWithEmpty() throws WaitException {

        PartialTuple t;

        t = PartialTuple.create(null, List.of());
        t.checkDetermined();
        assertFalse(t.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_TUPLE, t.unificationPriority());
        assertNull(t.futureLabel());
        assertEquals(Rec.DEFAULT_LABEL, t.label());
        assertEquals(0, t.futureFieldCount());
        assertEquals(0, t.fieldCount());
        assertEquals(0, t.totalFieldCount());
        assertNull(t.findValue(Int32.I32_0));
        assertNull(t.findValue(Str.of("not-a-feature")));
    }

    @Test
    public void testCreateWithEmptyWithFutureLabel() throws WaitVarException {

        PartialTuple t;

        Var labelVar = new Var();
        Str testLabel = Str.of("test-label");

        t = PartialTuple.create(labelVar, List.of());
        Collection<Var> unboundVars = t.sweepUndeterminedVars();
        assertEquals(1, unboundVars.size());
        assertEquals(labelVar, unboundVars.iterator().next());
        {
            Exception exc = assertThrows(WaitVarException.class, t::checkDetermined);
            assertNull(exc.getMessage());
        }

        labelVar.bindToValue(testLabel, null);
        unboundVars = t.sweepUndeterminedVars();
        assertEquals(0, unboundVars.size());
        t.checkDetermined();
        assertFalse(t.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_TUPLE, t.unificationPriority());
        assertEquals(testLabel, t.label());
    }

    @Test
    public void testCreateWithEmptyWithLabel() throws WaitException {

        PartialTuple t;

        Str testLabel = Str.of("test-label");

        t = PartialTuple.create(testLabel, List.of());
        t.checkDetermined();
        assertFalse(t.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_TUPLE, t.unificationPriority());
        assertNull(t.futureLabel());
        assertEquals(testLabel, t.label());
        assertEquals(0, t.futureFieldCount());
        assertEquals(0, t.fieldCount());
        assertEquals(0, t.totalFieldCount());
        assertNull(t.findValue(Int32.I32_0));
        assertNull(t.findValue(Str.of("not-a-feature")));
    }

    @Test
    public void testCreateWithPartial1() throws WaitException {

        PartialTuple t;
        Var aVar = new Var();
        Str a = Str.of("a");

        t = PartialTuple.create(null, List.of(aVar));
        t.checkDetermined();
        assertFalse(t.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_TUPLE, t.unificationPriority());
        assertNull(t.futureLabel());
        assertEquals(Rec.DEFAULT_LABEL, t.label());

        assertEquals(0, t.futureFieldCount());
        assertEquals(1, t.fieldCount());
        assertEquals(1, t.totalFieldCount());
        assertEquals(aVar, t.findValue(Int32.I32_0));
        {
            Exception exc = assertThrows(WaitVarException.class, t::checkComplete);
            assertNull(exc.getMessage());
        }
        aVar.bindToValue(a, null);
        Complete c = t.checkComplete();
        assertInstanceOf(CompleteTuple.class, c);
        CompleteTuple ct = (CompleteTuple) c;
        assertEquals(a, ct.select(Int32.I32_0));
    }

    @Test
    public void testCreateWithPartial2() throws WaitException {

        PartialTuple t;
        Var aVar = new Var();
        Str a = Str.of("a");
        Var bVar = new Var();
        Str b = Str.of("b");

        t = PartialTuple.create(null, List.of(aVar, bVar));
        t.checkDetermined();
        assertFalse(t.isValidKey());
        assertEquals(UnificationPriority.PARTIAL_TUPLE, t.unificationPriority());
        assertNull(t.futureLabel());
        assertEquals(Rec.DEFAULT_LABEL, t.label());

        assertEquals(0, t.futureFieldCount());
        assertEquals(2, t.fieldCount());
        assertEquals(2, t.totalFieldCount());

        assertEquals(aVar, t.findValue(Int32.I32_0));
        {
            Exception exc = assertThrows(WaitVarException.class, t::checkComplete);
            assertNull(exc.getMessage());
        }
        aVar.bindToValue(a, null);
        assertEquals(bVar, t.findValue(Int32.I32_1));
        {
            Exception exc = assertThrows(WaitVarException.class, t::checkComplete);
            assertNull(exc.getMessage());
        }
        bVar.bindToValue(b, null);
        Complete c = t.checkComplete();
        assertInstanceOf(CompleteTuple.class, c);
        CompleteTuple ct = (CompleteTuple) c;
        assertEquals(a, ct.select(Int32.I32_0));
        assertEquals(b, ct.select(Int32.I32_1));
    }

    @Test
    public void testFeatureNotFoundError0() {
        PartialTuple t1;

        Exception exc;

        t1 = PartialTuple.create(null, List.of());
        exc = assertThrows(FeatureNotFoundError.class, () -> t1.select(Int32.I32_0));
        assertEquals("Feature not found", exc.getMessage());
        exc = assertThrows(FeatureNotFoundError.class, () -> t1.select(Str.of("not-a-feature")));
        assertEquals("Feature not found", exc.getMessage());
    }

    @Test
    public void testFeatureNotFoundError1() {
        PartialTuple t1;
        Str a = Str.of("a");

        Exception exc;

        t1 = PartialTuple.create(null, List.of(a));
        exc = assertThrows(FeatureNotFoundError.class, () -> t1.select(Int32.I32_1));
        assertEquals("Feature not found", exc.getMessage());
        exc = assertThrows(FeatureNotFoundError.class, () -> t1.select(Str.of("not-a-feature")));
        assertEquals("Feature not found", exc.getMessage());
    }

    @Test
    public void testIndexException0() {
        PartialTuple t1;

        Exception exc;

        t1 = PartialTuple.create(null, List.of());
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
        PartialTuple t1;
        Str a = Str.of("a");

        Exception exc;

        t1 = PartialTuple.create(null, List.of(a));
        Field f = t1.fieldAt(0);
        assertEquals(Int32.I32_0, f.feature());
        assertEquals(a, f.value());
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> t1.fieldAt(1));
        assertEquals("Index 1 out of bounds for length 1", exc.getMessage());
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> t1.featureAt(1));
        assertEquals("Index 1 out of bounds for length 1", exc.getMessage());
        exc = assertThrows(ArrayIndexOutOfBoundsException.class, () -> t1.valueAt(1));
        assertEquals("Index 1 out of bounds for length 1", exc.getMessage());
        assertEquals(a, t1.findValue(Int32.I32_0));
        assertNull(t1.findValue(Int32.I32_1));
    }

}
