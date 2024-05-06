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

public class TestInt64 {

    private final static Int64 THREE = Int64.of(3);
    private final static Int64 FIVE = Int64.of(5);
    private final static Int64 FIVE_2 = Int64.of(5);
    private final static Int64 SEVEN = Int64.of(7);

    @Test
    public void testAppendToString() {
        assertEquals("3", THREE.appendToString(""));
        assertEquals("5", FIVE.appendToString(""));
        assertEquals("7", SEVEN.appendToString(""));
        assertEquals("X-3", THREE.appendToString("X-"));
        assertEquals("X-5", FIVE.appendToString("X-"));
        assertEquals("X-7", SEVEN.appendToString("X-"));
    }

    @Test
    public void testCompare() {
        assertTrue(THREE.compareValueTo(FIVE) < 0);
        assertEquals(0, FIVE.compareValueTo(FIVE_2));
        assertTrue(FIVE.compareValueTo(THREE) > 0);
        assertTrue(THREE.compareValueTo(Int64.I64_MIN) > 0);
        assertTrue(Int64.I64_MIN.compareValueTo(THREE) < 0);
        assertTrue(THREE.compareValueTo(Int64.I64_MAX) < 0);
        assertTrue(Int64.I64_MAX.compareValueTo(THREE) > 0);

        Exception exc = assertThrows(IllegalArgumentException.class, () -> THREE.compareValueTo(Str.of("foo")));
        assertEquals(Num.NOT_A_NUM, exc.getMessage());
    }

    @Test
    public void testConstants() {
        assertEquals(0, Int64.I64_0.longValue());
        assertEquals(1, Int64.I64_1.longValue());
        assertEquals(2, Int64.I64_2.longValue());
        assertEquals(3, Int64.I64_3.longValue());
        assertEquals(4, Int64.I64_4.longValue());
        assertEquals(5, Int64.I64_5.longValue());
        assertEquals(6, Int64.I64_6.longValue());
        assertEquals(7, Int64.I64_7.longValue());
        assertEquals(8, Int64.I64_8.longValue());
        assertEquals(9, Int64.I64_9.longValue());

        assertEquals(Long.MIN_VALUE, Int64.I64_MIN.longValue());
        assertEquals(Long.MAX_VALUE, Int64.I64_MAX.longValue());
    }

    @Test
    public void testCreate() {
        Int64 i;

        i = Int64.of(3);
        assertEquals(3, i.longValue());
        assertEquals(THREE, i);

        i = Int64.of(5);
        assertEquals(5, i.longValue());
        assertEquals(FIVE, i);

        i = Int64.of(7);
        assertEquals(7, i.longValue());
        assertEquals(SEVEN, i);
    }

    @Test
    public void testEntails() throws WaitException {
        assertFalse(THREE.entails(FIVE).value);
        assertTrue(FIVE.entails(FIVE_2).value);
        assertTrue(FIVE_2.entails(FIVE).value);
        assertFalse(FIVE.entails(Str.of("foo")).value);

        assertTrue(THREE.disentails(FIVE).value);
        assertFalse(FIVE.disentails(FIVE_2).value);
        assertFalse(FIVE_2.disentails(FIVE).value);
        assertTrue(FIVE.disentails(Str.of("foo")).value);
    }

    @Test
    public void testEquals() {
        assertEquals(FIVE, FIVE_2);
        assertEquals(FIVE_2, FIVE);
        assertNotEquals(FIVE, THREE);
        assertNotEquals(THREE, FIVE);
    }

    @Test
    public void testGreaterThan() {
        assertFalse(THREE.greaterThan(FIVE).value);
        assertTrue(FIVE.greaterThan(THREE).value);
        assertFalse(FIVE.greaterThan(FIVE_2).value);
        assertFalse(FIVE_2.greaterThan(FIVE).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> THREE.greaterThan(Str.of("foo")));
        assertEquals(Num.NOT_A_NUM, exc.getMessage());
    }

    @Test
    public void testGreaterThanOrEqualTo() {
        assertFalse(THREE.greaterThanOrEqualTo(FIVE).value);
        assertTrue(FIVE.greaterThanOrEqualTo(THREE).value);
        assertTrue(FIVE.greaterThanOrEqualTo(FIVE_2).value);
        assertTrue(FIVE_2.greaterThanOrEqualTo(FIVE).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> THREE.greaterThanOrEqualTo(Str.of("foo")));
        assertEquals(Num.NOT_A_NUM, exc.getMessage());
    }

    @Test
    public void testHashcode() {
        assertEquals(Long.hashCode(FIVE.longValue()), FIVE.hashCode());
        assertEquals(Long.hashCode(FIVE_2.longValue()), FIVE_2.hashCode());
        assertEquals(FIVE.hashCode(), FIVE_2.hashCode());
    }

    @Test
    public void testInt64HashKey() {
        Map<Int64, String> hm = new HashMap<>();
        hm.put(THREE, THREE.toString());
        hm.put(FIVE, FIVE.toString());
        hm.put(SEVEN, SEVEN.toString());
        assertEquals(3, hm.size());
        assertEquals(hm.get(THREE), THREE.toString());
        assertEquals(hm.get(FIVE), FIVE.toString());
        assertEquals(hm.get(SEVEN), SEVEN.toString());
        assertNotEquals(hm.get(THREE), SEVEN.toString());
        assertEquals(hm.remove(THREE), THREE.toString());
        assertEquals(2, hm.size());
        assertEquals(hm.remove(FIVE), FIVE.toString());
        assertEquals(1, hm.size());
        assertEquals(hm.remove(SEVEN), SEVEN.toString());
        assertEquals(0, hm.size());
    }

    @Test
    public void testIsValidKey() {
        assertTrue(THREE.isValidKey());
        assertTrue(FIVE.isValidKey());
        assertTrue(SEVEN.isValidKey());
    }

    @Test
    public void testLessThan() {
        assertTrue(THREE.lessThan(FIVE).value);
        assertFalse(FIVE.lessThan(THREE).value);
        assertFalse(FIVE.lessThan(FIVE_2).value);
        assertFalse(FIVE_2.lessThan(FIVE).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> THREE.lessThan(Str.of("foo")));
        assertEquals(Num.NOT_A_NUM, exc.getMessage());
    }

    @Test
    public void testLessThanOrEqualTo() {
        assertTrue(THREE.lessThanOrEqualTo(FIVE).value);
        assertFalse(FIVE.lessThanOrEqualTo(THREE).value);
        assertTrue(FIVE.lessThanOrEqualTo(FIVE_2).value);
        assertTrue(FIVE_2.lessThanOrEqualTo(FIVE).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> THREE.lessThanOrEqualTo(Str.of("foo")));
        assertEquals(Num.NOT_A_NUM, exc.getMessage());
    }

    @Test
    public void testMinMax() {
        Int64 i;

        i = Int64.of(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, i.longValue());

        i = Int64.of(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, i.longValue());
    }

    @Test
    public void testNegate() {
        Int64 i = Int64.of(3);
        assertEquals(-3, i.negate().longValue());
        assertEquals(3, i.negate().negate().longValue());
    }

    @Test
    public void testToValues() {

        assertEquals("3", THREE.toString());
        assertEquals("3", THREE.formatValue());
        assertEquals(3L, THREE.toNativeValue());
        assertEquals("3L", THREE.toKernelString());

        assertEquals("5", FIVE.toString());
        assertEquals("5", FIVE.formatValue());
        assertEquals(5L, FIVE.toNativeValue());
        assertEquals("5L", FIVE.toKernelString());

        assertEquals("7", SEVEN.toString());
        assertEquals("7", SEVEN.formatValue());
        assertEquals(7L, SEVEN.toNativeValue());
        assertEquals("7L", SEVEN.toKernelString());

        Int64 i;

        i = Int64.of(Long.MAX_VALUE);
        assertEquals("" + Long.MAX_VALUE, i.toString());

        i = Int64.of(Long.MIN_VALUE);
        assertEquals("" + Long.MIN_VALUE, i.toString());

        assertEquals(3, Int64.of(3).intValue());
        assertEquals(3L, Int64.of(3).longValue());
        assertEquals(3f, Int64.of(3).floatValue(), 0.000001);
        assertEquals(3d, Int64.of(3).doubleValue(), 0.000001);
        assertEquals(new BigDecimal(3, MathContext.DECIMAL128), Int64.of(3).decimal128Value());
    }

}
