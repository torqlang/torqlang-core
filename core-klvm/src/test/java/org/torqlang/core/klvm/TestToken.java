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

public class TestToken {

    private final Token t1 = new Token();
    private final Token t2 = new Token();
    private final Token t3 = new Token();

    @Test
    public void testAppendToString() {
        Token t;

        t = new Token();
        assertNotNull(t.appendToString(""));
        assertTrue(t.appendToString("X-").startsWith("X-<<$token "));
    }

    @Test
    public void testEntails() throws WaitException {
        assertFalse(t1.entails(t2).value);
        assertFalse(t2.entails(t1).value);
        assertFalse(t1.entails(t3).value);
        assertFalse(t3.entails(t1).value);
        assertFalse(t2.entails(t3).value);
        assertFalse(t3.entails(t2).value);

        assertTrue(t1.disentails(t2).value);
        assertTrue(t2.disentails(t1).value);
        assertTrue(t1.disentails(t3).value);
        assertTrue(t3.disentails(t1).value);
        assertTrue(t2.disentails(t3).value);
        assertTrue(t3.disentails(t2).value);
    }

    @Test
    public void testEquals() {
        assertNotEquals(t1, t2);
        assertNotEquals(t2, t1);
        assertNotEquals(t1, t3);
        assertNotEquals(t3, t1);
        assertNotEquals(t2, t3);
        assertNotEquals(t3, t2);
    }

    @Test
    public void testHashcode() {
        assertNotEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    public void testIsValidKey() {
        assertTrue(t1.isValidKey());
    }

    @Test
    public void testToValues() {
        assertTrue(t1.toString().startsWith("<<$token "));
        assertTrue(t1.formatValue().startsWith("<<$token "));
        assertTrue(t1.toNativeValue().startsWith("org.torqlang.core.klvm.Token."));
        assertTrue(t1.toKernelString().startsWith("<<$token "));
    }

    @Test
    public void testTokenHashKey() {
        Map<Token, String> hm = new HashMap<>();
        hm.put(t1, "1");
        hm.put(t2, "2");
        hm.put(t3, "3");
        assertEquals(3, hm.size());
        assertEquals(hm.get(t1), "1");
        assertEquals(hm.get(t2), "2");
        assertEquals(hm.get(t3), "3");
        assertNotEquals(hm.get(t1), "2");
        assertEquals(hm.remove(t1), "1");
        assertEquals(2, hm.size());
        assertEquals(hm.remove(t2), "2");
        assertEquals(1, hm.size());
        assertEquals(hm.remove(t3), "3");
        assertEquals(0, hm.size());
    }

}
