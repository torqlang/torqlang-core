/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.torqlang.core.klvm.Dec128.bigDecimal128;

public class TestFlt64 {

    private final static Flt64 THREE = Flt64.of(3d);
    private final static Flt64 FIVE = Flt64.of(5d);
    private final static Flt64 FIVE_2 = Flt64.of(5d);
    private final static Flt64 SEVEN = Flt64.of(7d);

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
        assertTrue(THREE.compareValueTo(Flt64.F64_MIN) > 0);
        assertTrue(Flt64.F64_MIN.compareValueTo(THREE) < 0);
        assertTrue(THREE.compareValueTo(Flt64.F64_MAX) < 0);
        assertTrue(Flt64.F64_MAX.compareValueTo(THREE) > 0);

        Exception exc = assertThrows(IllegalArgumentException.class, () -> THREE.compareValueTo(Str.of("foo")));
        assertEquals(Num.NOT_A_NUM, exc.getMessage());
    }

    @Test
    public void testConstants() {
        assertEquals(Double.MIN_VALUE, Flt64.F64_MIN.doubleValue(), 0.000001);
        assertEquals(Double.MAX_VALUE, Flt64.F64_MAX.doubleValue(), 0.000001);
    }

    @Test
    public void testCreate() {
        Flt64 f;

        f = Flt64.of(3d);
        assertEquals(3d, f.doubleValue(), 0.000001);
        assertEquals(THREE, f);

        f = Flt64.of(5d);
        assertEquals(5d, f.doubleValue(), 0.000001);
        assertEquals(FIVE, f);

        f = Flt64.of(7d);
        assertEquals(7d, f.doubleValue(), 0.000001);
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
    public void testFlt64HashKey() {
        Map<Flt64, String> hm = new HashMap<>();
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
        assertEquals(Double.hashCode(FIVE.intValue()), FIVE.hashCode());
        assertEquals(Double.hashCode(FIVE_2.intValue()), FIVE_2.hashCode());
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
        Flt64 f;

        f = Flt64.of(Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, f.doubleValue(), 0.000001);

        f = Flt64.of(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, f.doubleValue(), 0.000001);
    }

    @Test
    public void testNegate() {
        Flt64 i = Flt64.of(3d);
        assertEquals(-3d, i.negate().doubleValue(), 0.000001);
        assertEquals(3d, i.negate().negate().doubleValue(), 0.000001);
    }

    @Test
    public void testToValues() {

        assertEquals("3.0", THREE.toString());
        assertEquals("3.0", THREE.formatValue());
        assertEquals(3.0, THREE.toNativeValue());
        assertEquals("3.0", THREE.toKernelString());

        assertEquals("5.0", FIVE.toString());
        assertEquals("5.0", FIVE.formatValue());
        assertEquals(5.0, FIVE.toNativeValue());
        assertEquals("5.0", FIVE.toKernelString());

        assertEquals("7.0", SEVEN.toString());
        assertEquals("7.0", SEVEN.formatValue());
        assertEquals(7.0, SEVEN.toNativeValue());
        assertEquals("7.0", SEVEN.toKernelString());

        Flt64 i;

        i = Flt64.of(Double.MAX_VALUE);
        assertEquals("" + Double.MAX_VALUE, i.toString());

        i = Flt64.of(Double.MIN_VALUE);
        assertEquals("" + Double.MIN_VALUE, i.toString());

        assertEquals(3, Flt64.of(3d).intValue());
        assertEquals(3L, Flt64.of(3d).longValue());
        assertEquals(3f, Flt64.of(3d).floatValue(), 0.000001);
        assertEquals(3d, Flt64.of(3d).doubleValue(), 0.000001);
        assertEquals(bigDecimal128(3d), Flt64.of(3d).decimal128Value());
    }

}
