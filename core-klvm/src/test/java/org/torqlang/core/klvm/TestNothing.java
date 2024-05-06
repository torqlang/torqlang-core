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

public class TestNothing {

    @Test
    public void testAppendToString() {
        assertEquals("nothing", Nothing.SINGLETON.appendToString(""));
        assertEquals("X-nothing", Nothing.SINGLETON.appendToString("X-"));
    }

    @Test
    public void testEntails() throws WaitException {
        assertTrue(Nothing.SINGLETON.entails(Nothing.SINGLETON).value);
        assertFalse(Nothing.SINGLETON.entails(Int32.I32_0).value);
        assertFalse(Nothing.SINGLETON.disentails(Nothing.SINGLETON).value);
        assertTrue(Nothing.SINGLETON.disentails(Int32.I32_0).value);
    }

    @Test
    public void testEquals() throws WaitException {
        //noinspection EqualsWithItself
        assertEquals(Nothing.SINGLETON, Nothing.SINGLETON);
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(Nothing.SINGLETON, Int32.I32_0);
    }

    @Test
    public void testHashcode() {
        assertEquals(Nothing.SINGLETON.hashCode(), Nothing.SINGLETON.hashCode());
    }

    @Test
    public void testIsValidKey() {
        assertTrue(Nothing.SINGLETON.isValidKey());
    }

    @Test
    public void testToValues() {
        assertEquals("nothing", Nothing.SINGLETON.toString());
        assertEquals("nothing", Nothing.SINGLETON.formatValue());
        assertNull(Nothing.SINGLETON.toNativeValue());
        assertEquals("nothing", Nothing.SINGLETON.toKernelString());
    }

    @Test
    public void testUnitHashKey() {
        Map<Value, String> hm = new HashMap<>();
        hm.put(Nothing.SINGLETON, "nothing");
        assertEquals(1, hm.size());
        assertEquals(hm.get(Nothing.SINGLETON), "nothing");
        assertEquals(hm.remove(Nothing.SINGLETON), "nothing");
        assertEquals(0, hm.size());
    }

}
