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

public class TestDec128 {

    private final static Dec128 THREE = Dec128.of(3);
    private final static Dec128 FIVE = Dec128.of(5);
    private final static Dec128 FIVE_2 = Dec128.of(5);
    private final static Dec128 SEVEN = Dec128.of(7);

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
        assertTrue(THREE.compareValueTo(Dec128.D128_1) > 0);
        assertTrue(THREE.compareValueTo(Dec128.D128_10) < 0);
        assertTrue(Dec128.D128_1.compareValueTo(THREE) < 0);
        assertTrue(Dec128.D128_10.compareValueTo(THREE) > 0);

        Exception exc = assertThrows(IllegalArgumentException.class, () -> THREE.compareValueTo(Int32.I32_0));
        assertEquals("Not a Dec128", exc.getMessage());
    }

    @Test
    public void testConstants() {
        assertEquals(BigDecimal.ONE, Dec128.D128_1.decimal128Value());
        assertEquals(BigDecimal.TEN, Dec128.D128_10.decimal128Value());
    }

    @Test
    public void testCreate() {
        Dec128 i;

        i = Dec128.of(3);
        assertEquals(BigDecimal.valueOf(3), i.decimal128Value());
        assertEquals(THREE, i);

        i = Dec128.of(3d);
        assertEquals(new BigDecimal(Double.toString(3d), MathContext.DECIMAL128), i.decimal128Value());
        assertEquals(THREE, i);

        i = Dec128.of("3");
        assertEquals(BigDecimal.valueOf(3), i.decimal128Value());
        assertEquals(THREE, i);

        i = Dec128.of(5);
        assertEquals(BigDecimal.valueOf(5), i.decimal128Value());
        assertEquals(FIVE, i);

        i = Dec128.of(5d);
        assertEquals(new BigDecimal(Double.toString(5d), MathContext.DECIMAL128), i.decimal128Value());
        assertEquals(FIVE, i);

        i = Dec128.of("5");
        assertEquals(BigDecimal.valueOf(5), i.decimal128Value());
        assertEquals(FIVE, i);

        i = Dec128.of(7);
        assertEquals(BigDecimal.valueOf(7), i.decimal128Value());
        assertEquals(SEVEN, i);

        i = Dec128.of(7d);
        assertEquals(new BigDecimal(Double.toString(7d), MathContext.DECIMAL128), i.decimal128Value());
        assertEquals(SEVEN, i);

        i = Dec128.of("7");
        assertEquals(BigDecimal.valueOf(7), i.decimal128Value());
        assertEquals(SEVEN, i);
    }

    @Test
    public void testDec128HashKey() {
        Map<Dec128, String> hm = new HashMap<>();
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
    public void testDecode() {
        assertEquals(THREE, Dec128.decode("3"));
        assertEquals(THREE, Dec128.decode("3m"));
        assertEquals(THREE, Dec128.decode("0x03m"));
        assertEquals(FIVE, Dec128.decode("5"));
        assertEquals(FIVE, Dec128.decode("5m"));
        assertEquals(FIVE, Dec128.decode("0x05m"));
        assertEquals(SEVEN, Dec128.decode("7"));
        assertEquals(SEVEN, Dec128.decode("7m"));
        assertEquals(SEVEN, Dec128.decode("0x07m"));

        assertEquals(Dec128.of("0"), Dec128.decode("0m"));
        assertEquals(Dec128.of("1"), Dec128.decode("1m"));
        assertEquals(Dec128.of("10"), Dec128.decode("10m"));
        assertEquals(Dec128.of("100"), Dec128.decode("100m"));
        assertEquals(Dec128.of("1000"), Dec128.decode("1000m"));
        assertEquals(Dec128.of("10000"), Dec128.decode("10000m"));
        assertEquals(Dec128.of("100000"), Dec128.decode("100000m"));

        assertEquals(Dec128.of("0"), Dec128.decode("0x00m"));
        assertEquals(Dec128.of("1"), Dec128.decode("0x01m"));
        assertEquals(Dec128.of("16"), Dec128.decode("0x10m"));
        assertEquals(Dec128.of("256"), Dec128.decode("0x0100m"));
        assertEquals(Dec128.of("4096"), Dec128.decode("0x1000m"));
        assertEquals(Dec128.of("65536"), Dec128.decode("0x010000m"));
        assertEquals(Dec128.of("1048576"), Dec128.decode("0x100000m"));
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
        assertEquals(FIVE.value().hashCode(), FIVE.hashCode());
        assertEquals(FIVE_2.value().hashCode(), FIVE_2.hashCode());
        assertEquals(FIVE.value().hashCode(), FIVE_2.value().hashCode());
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
    public void testNegate() {
        Dec128 i = Dec128.of(3);
        assertEquals(BigDecimal.valueOf(-3), i.negate().decimal128Value());
        assertEquals(BigDecimal.valueOf(3), i.negate().negate().decimal128Value());
    }

    @Test
    public void testToValues() {

        assertEquals("3", THREE.toString());
        assertEquals("3", THREE.formatValue());
        assertEquals(BigDecimal.valueOf(3), THREE.toNativeValue());
        assertEquals("3m", THREE.toKernelString());

        assertEquals("5", FIVE.toString());
        assertEquals("5", FIVE.formatValue());
        assertEquals(BigDecimal.valueOf(5), FIVE.toNativeValue());
        assertEquals("5m", FIVE.toKernelString());

        assertEquals("7", SEVEN.toString());
        assertEquals("7", SEVEN.formatValue());
        assertEquals(BigDecimal.valueOf(7), SEVEN.toNativeValue());
        assertEquals("7m", SEVEN.toKernelString());

        assertEquals(3, Dec128.of(3).intValue());
        assertEquals(3L, Dec128.of(3).longValue());
        assertEquals(3f, Dec128.of(3).floatValue(), 0.000001);
        assertEquals(3d, Dec128.of(3).doubleValue(), 0.000001);
        assertEquals(new BigDecimal(3, MathContext.DECIMAL128), Dec128.of(3).decimal128Value());
    }

}
