/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.torqlang.core.klvm.Dec128.bigDecimal128;

public class TestFlt32 {

    private final static Flt32 THREE = Flt32.of(3f);
    private final static Flt32 FIVE = Flt32.of(5f);
    private final static Flt32 FIVE_2 = Flt32.of(5f);
    private final static Flt32 SEVEN = Flt32.of(7f);

    @Test
    public void testAppendToString() {
        assertEquals("3.0", THREE.appendToString(""));
        assertEquals("5.0", FIVE.appendToString(""));
        assertEquals("7.0", SEVEN.appendToString(""));
        assertEquals("X-3.0", THREE.appendToString("X-"));
        assertEquals("X-5.0", FIVE.appendToString("X-"));
        assertEquals("X-7.0", SEVEN.appendToString("X-"));
    }

    @Test
    public void testCompare() {
        assertTrue(THREE.compareValueTo(FIVE) < 0);
        assertEquals(0, FIVE.compareValueTo(FIVE_2));
        assertTrue(FIVE.compareValueTo(THREE) > 0);
        assertTrue(THREE.compareValueTo(Flt32.F32_MIN) > 0);
        assertTrue(Flt32.F32_MIN.compareValueTo(THREE) < 0);
        assertTrue(THREE.compareValueTo(Flt32.F32_MAX) < 0);
        assertTrue(Flt32.F32_MAX.compareValueTo(THREE) > 0);

        Exception exc = assertThrows(IllegalArgumentException.class, () -> THREE.compareValueTo(Str.of("foo")));
        assertEquals(Num.NOT_A_NUM, exc.getMessage());
    }

    @Test
    public void testConstants() {
        assertEquals(Float.MIN_VALUE, Flt32.F32_MIN.floatValue(), 0.000001);
        assertEquals(Float.MAX_VALUE, Flt32.F32_MAX.floatValue(), 0.000001);
    }

    @Test
    public void testCreate() {
        Flt32 f;

        f = Flt32.of(3f);
        assertEquals(3f, f.floatValue(), 0.000001);
        assertEquals(THREE, f);

        f = Flt32.of(5f);
        assertEquals(5f, f.floatValue(), 0.000001);
        assertEquals(FIVE, f);

        f = Flt32.of(7f);
        assertEquals(7f, f.floatValue(), 0.000001);
        assertEquals(SEVEN, f);
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
    public void testFlt32HashKey() {
        Map<Flt32, String> hm = new HashMap<>();
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
        assertEquals(Float.hashCode(FIVE.intValue()), FIVE.hashCode());
        assertEquals(Float.hashCode(FIVE_2.intValue()), FIVE_2.hashCode());
        assertEquals(FIVE.hashCode(), FIVE_2.hashCode());
    }

    @Test
    public void testIsValidKey() {
        assertFalse(THREE.isValidKey());
        assertFalse(FIVE.isValidKey());
        assertFalse(SEVEN.isValidKey());
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
        Flt32 f;

        f = Flt32.of(Float.MIN_VALUE);
        assertEquals(Float.MIN_VALUE, f.floatValue(), 0.000001);

        f = Flt32.of(Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, f.floatValue(), 0.000001);
    }

    @Test
    public void testNegate() {
        Flt32 i = Flt32.of(3f);
        assertEquals(-3f, i.negate().floatValue(), 0.000001);
        assertEquals(3f, i.negate().negate().floatValue(), 0.000001);
    }

    @Test
    public void testToValues() {

        assertEquals("3.0", THREE.toString());
        assertEquals("3.0", THREE.formatValue());
        assertEquals(3.0f, THREE.toNativeValue());
        assertEquals("3.0f", THREE.toKernelString());

        assertEquals("5.0", FIVE.toString());
        assertEquals("5.0", FIVE.formatValue());
        assertEquals(5.0f, FIVE.toNativeValue());
        assertEquals("5.0f", FIVE.toKernelString());

        assertEquals("7.0", SEVEN.toString());
        assertEquals("7.0", SEVEN.formatValue());
        assertEquals(7.0f, SEVEN.toNativeValue());
        assertEquals("7.0f", SEVEN.toKernelString());

        Flt32 i;

        i = Flt32.of(Float.MAX_VALUE);
        assertEquals("" + Float.MAX_VALUE, i.toString());

        i = Flt32.of(Float.MIN_VALUE);
        assertEquals("" + Float.MIN_VALUE, i.toString());

        assertEquals(3, Flt32.of(3f).intValue());
        assertEquals(3L, Flt32.of(3f).longValue());
        assertEquals(3f, Flt32.of(3f).floatValue(), 0.000001);
        assertEquals(3d, Flt32.of(3f).doubleValue(), 0.000001);
        assertEquals(bigDecimal128(3f), Flt32.of(3f).decimal128Value());
    }

}
