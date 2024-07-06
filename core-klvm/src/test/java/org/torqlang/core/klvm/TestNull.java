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

public class TestNull {

    @Test
    public void testAppendToString() {
        assertEquals("null", Null.SINGLETON.appendToString(""));
        assertEquals("X-null", Null.SINGLETON.appendToString("X-"));
    }

    @Test
    public void testEntails() throws WaitException {
        assertTrue(Null.SINGLETON.entails(Null.SINGLETON).value);
        assertFalse(Null.SINGLETON.entails(Int32.I32_0).value);
        assertFalse(Null.SINGLETON.disentails(Null.SINGLETON).value);
        assertTrue(Null.SINGLETON.disentails(Int32.I32_0).value);
    }

    @Test
    public void testEquals() throws WaitException {
        //noinspection EqualsWithItself
        assertEquals(Null.SINGLETON, Null.SINGLETON);
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(Null.SINGLETON, Int32.I32_0);
    }

    @Test
    public void testHashcode() {
        assertEquals(Null.SINGLETON.hashCode(), Null.SINGLETON.hashCode());
    }

    @Test
    public void testIsValidKey() {
        assertTrue(Null.SINGLETON.isValidKey());
    }

    @Test
    public void testNullHashKey() {
        Map<Value, String> hm = new HashMap<>();
        hm.put(Null.SINGLETON, "null");
        assertEquals(1, hm.size());
        assertEquals(hm.get(Null.SINGLETON), "null");
        assertEquals(hm.remove(Null.SINGLETON), "null");
        assertEquals(0, hm.size());
    }

    @Test
    public void testToValues() {
        assertEquals("null", Null.SINGLETON.toString());
        assertEquals("null", Null.SINGLETON.formatValue());
        assertNull(Null.SINGLETON.toNativeValue());
        assertEquals("null", Null.SINGLETON.toKernelString());
    }

}
