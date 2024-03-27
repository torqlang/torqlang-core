/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.klvm;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestBool {

    @Test
    public void testAppendToString() {
        assertEquals("false", Bool.FALSE.appendToString(""));
        assertEquals("true", Bool.TRUE.appendToString(""));
        assertEquals("X-false", Bool.FALSE.appendToString("X-"));
        assertEquals("X-true", Bool.TRUE.appendToString("X-"));
    }

    @Test
    public void testCreate() {
        //noinspection SimplifiableAssertion
        assertEquals(true, Bool.TRUE.value);
        //noinspection SimplifiableAssertion
        assertEquals(false, Bool.FALSE.value);

        Bool t = Bool.of(true);
        assertEquals(t, Bool.TRUE);
        assertTrue(t.value);

        Bool f = Bool.of(false);
        assertEquals(f, Bool.FALSE);
        assertFalse(f.value);
    }

    @Test
    public void testEntails() throws WaitException {

        assertTrue(Bool.TRUE.entails(Bool.TRUE).value);
        assertTrue(Bool.FALSE.entails(Bool.FALSE).value);
        assertFalse(Bool.TRUE.entails(Bool.FALSE).value);
        assertFalse(Bool.FALSE.entails(Bool.TRUE).value);

        assertFalse(Bool.TRUE.disentails(Bool.TRUE).value);
        assertFalse(Bool.FALSE.disentails(Bool.FALSE).value);
        assertTrue(Bool.TRUE.disentails(Bool.FALSE).value);
        assertTrue(Bool.FALSE.disentails(Bool.TRUE).value);

        assertFalse(Bool.TRUE.entails(Str.of("foo")).value);
        assertTrue(Bool.TRUE.disentails(Str.of("foo")).value);
    }

    @Test
    public void testEquals() {
        //noinspection EqualsWithItself
        assertEquals(Bool.TRUE, Bool.TRUE);
        assertNotEquals(Bool.TRUE, Bool.FALSE);
        //noinspection EqualsWithItself
        assertEquals(Bool.FALSE, Bool.FALSE);
        assertNotEquals(Bool.FALSE, Bool.TRUE);

        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(Bool.TRUE, Int32.I32_1);
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(Int32.I32_1, Bool.TRUE);
    }

    @Test
    public void testGreaterThan() {
        assertFalse(Bool.FALSE.greaterThan(Bool.TRUE).value);
        assertTrue(Bool.TRUE.greaterThan(Bool.FALSE).value);
        assertFalse(Bool.FALSE.greaterThan(Bool.FALSE).value);
        assertFalse(Bool.TRUE.greaterThan(Bool.TRUE).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> Bool.FALSE.greaterThan(Int32.of(99)));
        assertEquals(KlvmMessageText.ARGUMENT_MUST_BE_A_BOOL, exc.getMessage());
    }

    @Test
    public void testGreaterThanOrEqualTo() {
        assertFalse(Bool.FALSE.greaterThanOrEqualTo(Bool.TRUE).value);
        assertTrue(Bool.TRUE.greaterThanOrEqualTo(Bool.FALSE).value);
        assertTrue(Bool.FALSE.greaterThanOrEqualTo(Bool.FALSE).value);
        assertTrue(Bool.TRUE.greaterThanOrEqualTo(Bool.TRUE).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> Bool.FALSE.greaterThanOrEqualTo(Int32.of(99)));
        assertEquals(KlvmMessageText.ARGUMENT_MUST_BE_A_BOOL, exc.getMessage());
    }

    @Test
    public void testHashcode() {
        int th1 = Bool.TRUE.hashCode();
        int th2 = Bool.TRUE.hashCode();
        assertEquals(th1, th2);
        int fh1 = Bool.FALSE.hashCode();
        int fh2 = Bool.FALSE.hashCode();
        assertEquals(fh1, fh2);
        assertNotEquals(Bool.TRUE.hashCode(), Bool.FALSE.hashCode());
        assertEquals(Bool.TRUE.hashCode(), Boolean.TRUE.hashCode());
        assertEquals(Bool.FALSE.hashCode(), Boolean.FALSE.hashCode());
    }

    @Test
    public void testLessThan() {
        assertTrue(Bool.FALSE.lessThan(Bool.TRUE).value);
        assertFalse(Bool.TRUE.lessThan(Bool.FALSE).value);
        assertFalse(Bool.FALSE.lessThan(Bool.FALSE).value);
        assertFalse(Bool.TRUE.lessThan(Bool.TRUE).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> Bool.FALSE.lessThan(Int32.of(99)));
        assertEquals(KlvmMessageText.ARGUMENT_MUST_BE_A_BOOL, exc.getMessage());
    }

    @Test
    public void testLessThanOrEqualTo() {
        assertTrue(Bool.FALSE.lessThanOrEqualTo(Bool.TRUE).value);
        assertFalse(Bool.TRUE.lessThanOrEqualTo(Bool.FALSE).value);
        assertTrue(Bool.FALSE.lessThanOrEqualTo(Bool.FALSE).value);
        assertTrue(Bool.TRUE.lessThanOrEqualTo(Bool.TRUE).value);
        Exception exc = assertThrows(IllegalArgumentException.class, () -> Bool.FALSE.lessThanOrEqualTo(Int32.of(99)));
        assertEquals(KlvmMessageText.ARGUMENT_MUST_BE_A_BOOL, exc.getMessage());
    }

    @Test
    public void testNot() {
        assertTrue(Bool.FALSE.not().value);
        assertFalse(Bool.TRUE.not().value);
    }

    @Test
    public void testIsValidKey() {
        assertTrue(Bool.FALSE.isValidKey());
        assertTrue(Bool.TRUE.isValidKey());
    }

    @Test
    public void testToValues() {

        assertEquals("false", Bool.FALSE.toString());
        assertEquals("false", Bool.FALSE.toKernelString());
        assertEquals(Boolean.FALSE, Bool.FALSE.toNativeValue());

        assertEquals("true", Bool.TRUE.toString());
        assertEquals("true", Bool.TRUE.toKernelString());
        assertEquals(Boolean.TRUE, Bool.TRUE.toNativeValue());

        assertEquals("false", Bool.FALSE.formatValue());
        assertEquals("true", Bool.TRUE.formatValue());
    }

}
