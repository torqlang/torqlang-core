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

public class TestEof {

    @Test
    public void testAppendToString() {
        assertEquals("eof", Eof.SINGLETON.appendToString(""));
        assertEquals("X-eof", Eof.SINGLETON.appendToString("X-"));
    }

    @Test
    public void testEntails() throws WaitException {
        assertTrue(Eof.SINGLETON.entails(Eof.SINGLETON).value);
        assertFalse(Eof.SINGLETON.entails(Int32.I32_0).value);
        assertFalse(Eof.SINGLETON.disentails(Eof.SINGLETON).value);
        assertTrue(Eof.SINGLETON.disentails(Int32.I32_0).value);
    }

    @Test
    public void testEofHashKey() {
        Map<Value, String> hm = new HashMap<>();
        hm.put(Eof.SINGLETON, "eof");
        assertEquals(1, hm.size());
        assertEquals(hm.get(Eof.SINGLETON), "eof");
        assertEquals(hm.remove(Eof.SINGLETON), "eof");
        assertEquals(0, hm.size());
    }

    @Test
    public void testEquals() throws WaitException {
        //noinspection EqualsWithItself
        assertEquals(Eof.SINGLETON, Eof.SINGLETON);
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(Eof.SINGLETON, Int32.I32_0);
    }

    @Test
    public void testHashcode() {
        assertEquals(Eof.SINGLETON.hashCode(), Eof.SINGLETON.hashCode());
    }

    @Test
    public void testIsValidKey() {
        assertTrue(Eof.SINGLETON.isValidKey());
    }

    @Test
    public void testToValues() {
        assertEquals("eof", Eof.SINGLETON.toString());
        assertEquals("eof", Eof.SINGLETON.formatValue());
        assertEquals("org.torqlang.core.klvm.Eof", Eof.SINGLETON.toNativeValue());
        assertEquals("eof", Eof.SINGLETON.toKernelString());
    }

}
