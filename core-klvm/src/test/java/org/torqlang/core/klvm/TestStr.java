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

public class TestStr {

    private final static Str THREE = Str.of("THREE");
    private final static Str FIVE = Str.of("FIVE");
    private final static Str FIVE_2 = Str.of("FIVE");
    private final static Str SEVEN = Str.of("SEVEN");

    @Test
    public void testAppendToString() {
        assertEquals("THREE", THREE.appendToString(""));
        assertEquals("FIVE", FIVE.appendToString(""));
        assertEquals("SEVEN", SEVEN.appendToString(""));
        assertEquals("X-THREE", THREE.appendToString("X-"));
        assertEquals("X-FIVE", FIVE.appendToString("X-"));
        assertEquals("X-SEVEN", SEVEN.appendToString("X-"));

        assertEquals(THREE, Str.of("").add(THREE));
        assertEquals(FIVE, Str.of("").add(FIVE));
        assertEquals(SEVEN, Str.of("").add(SEVEN));

        assertEquals(Str.of("a"), Str.of("").add(Char.of('a')));
        assertEquals(Str.of("3"), Str.of("").add(Int32.of(3)));
        assertEquals(Str.of("30"), Str.of("").add(Int64.of(30)));
        assertEquals(Str.of("300.0"), Str.of("").add(Flt32.of(300.0f)));
        assertEquals(Str.of("3000.0"), Str.of("").add(Flt64.of(3000.0d)));
    }

    @Test
    public void testCompare() {
        assertTrue(THREE.compareValueTo(FIVE) > 0);
        assertEquals(0, FIVE.compareValueTo(FIVE_2));
        assertTrue(FIVE.compareValueTo(THREE) < 0);

        Exception exc = assertThrows(IllegalArgumentException.class, () -> THREE.compareValueTo(Int64.I64_0));
        assertEquals("Argument must be a Str", exc.getMessage());
    }

    @Test
    public void testCreate() {
        Str s;

        s = Str.of("test string");
        assertEquals("test string", s.toString());

        s = Str.of("test \u0001 string");
        assertEquals("test \u0001 string", s.toString());

        Exception exc = assertThrows(NullPointerException.class, () -> Str.of(null));
        assertEquals("value", exc.getMessage());
    }

    @Test
    public void testEntails() throws WaitException {
        assertFalse(THREE.entails(FIVE).value);
        assertTrue(FIVE.entails(FIVE_2).value);
        assertTrue(FIVE_2.entails(FIVE).value);
        assertFalse(FIVE.entails(Int32.I32_0).value);

        assertTrue(THREE.disentails(FIVE).value);
        assertFalse(FIVE.disentails(FIVE_2).value);
        assertFalse(FIVE_2.disentails(FIVE).value);
        assertTrue(FIVE.disentails(Int32.I32_0).value);
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
        assertTrue(THREE.greaterThan(FIVE).value);
        assertFalse(FIVE.greaterThan(THREE).value);
        assertFalse(FIVE.greaterThan(FIVE_2).value);
        assertFalse(FIVE_2.greaterThan(FIVE).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> THREE.greaterThan(Int32.I32_0));
        assertEquals("Argument must be a Str", exc.getMessage());
    }

    @Test
    public void testGreaterThanOrEqualTo() {
        assertTrue(THREE.greaterThanOrEqualTo(FIVE).value);
        assertFalse(FIVE.greaterThanOrEqualTo(THREE).value);
        assertTrue(FIVE.greaterThanOrEqualTo(FIVE_2).value);
        assertTrue(FIVE_2.greaterThanOrEqualTo(FIVE).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> THREE.greaterThanOrEqualTo(Int32.I32_0));
        assertEquals("Argument must be a Str", exc.getMessage());
    }

    @Test
    public void testHashcode() {
        assertEquals(FIVE.toNativeValue().hashCode(), FIVE.hashCode());
        assertEquals(FIVE_2.toNativeValue().hashCode(), FIVE_2.hashCode());
        assertEquals(FIVE.hashCode(), FIVE_2.hashCode());
    }

    @Test
    public void testInt32HashKey() {
        Map<Str, String> hm = new HashMap<>();
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
        assertFalse(THREE.lessThan(FIVE).value);
        assertTrue(FIVE.lessThan(THREE).value);
        assertFalse(FIVE.lessThan(FIVE_2).value);
        assertFalse(FIVE_2.lessThan(FIVE).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> THREE.lessThan(Int32.I32_0));
        assertEquals("Argument must be a Str", exc.getMessage());
    }

    @Test
    public void testLessThanOrEqualTo() {
        assertFalse(THREE.lessThanOrEqualTo(FIVE).value);
        assertTrue(FIVE.lessThanOrEqualTo(THREE).value);
        assertTrue(FIVE.lessThanOrEqualTo(FIVE_2).value);
        assertTrue(FIVE_2.lessThanOrEqualTo(FIVE).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> THREE.lessThanOrEqualTo(Int32.I32_0));
        assertEquals("Argument must be a Str", exc.getMessage());
    }

    @Test
    public void testSelect() {
        Value value = THREE.select(Str.of("substring"));
        assertNotNull(value);
        assertInstanceOf(ObjProcBinding.class, value);
    }

    @Test
    public void testToValues() {
        assertEquals("THREE", THREE.toString());
        assertEquals("THREE", THREE.formatValue());
        assertEquals("THREE", THREE.toNativeValue());
        assertEquals("'THREE'", THREE.toKernelString());

        assertEquals("FIVE", FIVE.toString());
        assertEquals("FIVE", FIVE.formatValue());
        assertEquals("FIVE", FIVE.toNativeValue());
        assertEquals("'FIVE'", FIVE.toKernelString());

        assertEquals("SEVEN", SEVEN.toString());
        assertEquals("SEVEN", SEVEN.formatValue());
        assertEquals("SEVEN", SEVEN.toNativeValue());
        assertEquals("'SEVEN'", SEVEN.toKernelString());
    }

}
