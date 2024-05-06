/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestChar {

    private final static Char A = Char.of('A');
    private final static Char C = Char.of('C');
    private final static Char C_2 = Char.of('C');
    private final static Char E = Char.of('E');

    @Test
    public void testAppendToString() {
        assertEquals("A", A.appendToString(""));
        assertEquals("C", C.appendToString(""));
        assertEquals("E", E.appendToString(""));
        assertEquals("X-A", A.appendToString("X-"));
        assertEquals("X-C", C.appendToString("X-"));
        assertEquals("X-E", E.appendToString("X-"));
    }

    @Test
    public void testCompare() {
        assertTrue(A.compareValueTo(C) < 0);
        assertEquals(0, C.compareValueTo(C_2));
        assertTrue(C.compareValueTo(A) > 0);

        assertTrue(A.compareValueTo(Char.CHAR_MIN) > 0);
        assertTrue(Char.CHAR_MIN.compareValueTo(A) < 0);
        assertTrue(A.compareValueTo(Char.CHAR_MAX) < 0);
        assertTrue(Char.CHAR_MAX.compareValueTo(A) > 0);

        Exception exc = assertThrows(IllegalArgumentException.class, () -> A.compareValueTo(Str.of("foo")));
        assertEquals(Num.NOT_A_NUM, exc.getMessage());
    }

    @Test
    public void testCreate() {
        Char c = Char.of('x');
        assertEquals('x', c.charValue());
        int intValue = 'x';
        assertEquals(intValue, c.intValue());
    }

    @Test
    public void testEntails() throws WaitException {
        assertFalse(A.entails(C).value);
        assertTrue(C.entails(C_2).value);
        assertTrue(C_2.entails(C).value);
        assertFalse(A.entails(Str.of("foo")).value);

        assertTrue(A.disentails(C).value);
        assertFalse(C.disentails(C_2).value);
        assertFalse(C_2.disentails(C).value);
        assertTrue(A.disentails(Str.of("foo")).value);
    }

    @Test
    public void testEquals() {
        assertEquals(C, C_2);
        assertEquals(C_2, C);
        assertNotEquals(A, C);
        assertNotEquals(C, A);
    }

    @Test
    public void testGreaterThan() {
        assertFalse(A.greaterThan(C).value);
        assertTrue(C.greaterThan(A).value);
        assertFalse(C.greaterThan(C_2).value);
        assertFalse(C_2.greaterThan(C).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> C.greaterThan(Str.of("foo")));
        assertEquals(Num.NOT_A_NUM, exc.getMessage());
    }

    @Test
    public void testGreaterThanOrEqualTo() {
        assertFalse(A.greaterThanOrEqualTo(C).value);
        assertTrue(C.greaterThanOrEqualTo(A).value);
        assertTrue(C.greaterThanOrEqualTo(C_2).value);
        assertTrue(C_2.greaterThanOrEqualTo(C).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> A.greaterThanOrEqualTo(Str.of("foo")));
        assertEquals(Num.NOT_A_NUM, exc.getMessage());
    }

    @Test
    public void testHashcode() {
        assertEquals(Character.valueOf('A').hashCode(), A.hashCode());
        assertEquals(Character.valueOf('C').hashCode(), C.hashCode());
        assertEquals(Character.valueOf('E').hashCode(), E.hashCode());
    }

    @Test
    public void testInt32HashKey() {
        Map<Int32, String> hm = new HashMap<>();
        hm.put(A, A.toString());
        hm.put(C, C.toString());
        hm.put(E, E.toString());
        assertEquals(3, hm.size());
        assertEquals(hm.get(A), A.toString());
        assertEquals(hm.get(C), C.toString());
        assertEquals(hm.get(E), E.toString());
        assertNotEquals(hm.get(A), E.toString());
        assertEquals(hm.remove(A), A.toString());
        assertEquals(2, hm.size());
        assertEquals(hm.remove(C), C.toString());
        assertEquals(1, hm.size());
        assertEquals(hm.remove(E), E.toString());
        assertEquals(0, hm.size());
    }

    @Test
    public void testIsValidKey() {
        assertTrue(A.isValidKey());
        assertTrue(C.isValidKey());
        assertTrue(E.isValidKey());
    }

    @Test
    public void testLessThan() {
        assertTrue(A.lessThan(C).value);
        assertFalse(C.lessThan(A).value);
        assertFalse(C.lessThan(C_2).value);
        assertFalse(C_2.lessThan(C).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> A.lessThan(Str.of("foo")));
        assertEquals(Num.NOT_A_NUM, exc.getMessage());
    }

    @Test
    public void testLessThanOrEqualTo() {
        assertTrue(A.lessThanOrEqualTo(C).value);
        assertFalse(C.lessThanOrEqualTo(A).value);
        assertTrue(C.lessThanOrEqualTo(C_2).value);
        assertTrue(C_2.lessThanOrEqualTo(C).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> A.lessThanOrEqualTo(Str.of("foo")));
        assertEquals(Num.NOT_A_NUM, exc.getMessage());
    }

    @Test
    public void testMinMax() {
        Int32 i;

        i = Int32.of(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, i.intValue());

        i = Int32.of(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, i.intValue());
    }

    @Test
    public void testNegate() {
        Char c = Char.of('\u0001');
        assertEquals(-1, c.negate().intValue());
        assertEquals(1, c.negate().negate().intValue());
    }

    @Test
    public void testToValues() {

        assertEquals("A", A.toString());
        assertEquals("A", A.formatValue());
        assertEquals(Character.valueOf('A'), A.toNativeValue());
        assertEquals("&A", A.toKernelString());

        assertEquals("C", C.toString());
        assertEquals("C", C.formatValue());
        assertEquals(Character.valueOf('C'), C.toNativeValue());
        assertEquals("&C", C.toKernelString());

        assertEquals("E", E.toString());
        assertEquals("E", E.formatValue());
        assertEquals(Character.valueOf('E'), E.toNativeValue());
        assertEquals("&E", E.toKernelString());

        Char c;

        c = Char.of(Character.MAX_VALUE);
        assertEquals("" + Character.MAX_VALUE, c.toString());

        c = Char.of(Character.MIN_VALUE);
        assertEquals("" + Character.MIN_VALUE, c.toString());

        assertEquals('\u0003', Char.of('\u0003').charValue());
        assertEquals(3, Char.of('\u0003').intValue());
        assertEquals(3L, Char.of('\u0003').longValue());
        assertEquals(3f, Char.of('\u0003').floatValue(), 0.000001);
        assertEquals(3d, Char.of('\u0003').doubleValue(), 0.000001);
        assertEquals(new BigDecimal(3, MathContext.DECIMAL128), Char.of('\u0003').decimal128Value());
    }

}
