/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestJsonParser {

    @SuppressWarnings("rawtypes")
    @Test
    public void testArray() {
        String source = """
            [0, 1, -1, 0.0, 1.0, false, true, null, "my-string", [], {}]""";
        Object jv = JsonParser.parse(source);
        assertInstanceOf(List.class, jv);
        List a = (List) jv;
        assertEquals(11, a.size());
        assertEquals(0L, a.get(0));
        assertEquals(1L, a.get(1));
        assertEquals(-1L, a.get(2));
        assertEquals(0.0, a.get(3));
        assertEquals(1.0, a.get(4));
        assertEquals(Boolean.FALSE, a.get(5));
        assertEquals(Boolean.TRUE, a.get(6));
        assertEquals(JsonNull.SINGLETON, a.get(7));
        assertEquals("my-string", a.get(8));
        assertEquals(List.of(), a.get(9));
        assertEquals(Map.of(), a.get(10));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testArrayEmpty() {
        Object jv = JsonParser.parse("[]");
        assertInstanceOf(List.class, jv);
        List a = (List) jv;
        assertTrue(a.isEmpty());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testArrayEmptyArray() {
        Object jv = JsonParser.parse("[[]]");
        assertInstanceOf(List.class, jv);
        List a = (List) jv;
        assertEquals(1, a.size());
        assertInstanceOf(List.class, a.get(0));
        assertEquals(List.of(), a.get(0));
    }

    @Test
    public void testBoolean() {
        Object jv = JsonParser.parse("true");
        assertInstanceOf(Boolean.class, jv);
        boolean b = (Boolean) jv;
        assertTrue(b);
        jv = JsonParser.parse("false");
        assertInstanceOf(Boolean.class, jv);
        b = (Boolean) jv;
        assertFalse(b);
    }

    @Test
    public void testErrors() {
        {
            JsonParser p = new JsonParser("{1:1}");
            Exception exc = assertThrows(IllegalArgumentException.class, p::parse);
            assertEquals("String expected - [1, 2] NUMBER: 1", exc.getMessage());
        }
        {
            String source = """
                {"one","two"}""";
            JsonParser p = new JsonParser(source);
            Exception exc = assertThrows(IllegalArgumentException.class, p::parse);
            assertEquals(": expected - [6, 7] DELIMITER: ,", exc.getMessage());
        }
        {
            JsonParser p = new JsonParser("[0,,]");
            Exception exc = assertThrows(IllegalArgumentException.class, p::parse);
            assertEquals("Unexpected delimiter: ,", exc.getMessage());
        }
        {
            JsonParser p = new JsonParser("wrong");
            Exception exc = assertThrows(IllegalArgumentException.class, p::parse);
            assertEquals("Invalid keyword -- not a true, false, or null", exc.getMessage());
        }
    }

    @Test
    public void testInteger() {
        Object jv = JsonParser.parse("0");
        assertInstanceOf(Long.class, jv);
        long i = (Long) jv;
        assertEquals(0, i);
        jv = JsonParser.parse("1");
        assertInstanceOf(Long.class, jv);
        i = (Long) jv;
        assertEquals(1, i);
        jv = JsonParser.parse("-1");
        assertInstanceOf(Long.class, jv);
        i = (Long) jv;
        assertEquals(-1, i);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testObject() {
        String source = """
            {"a": 0, "b": 1, "c": -1, "d": 0.0, "e": 1.0, "f": false, "g": true, "h": null,
            "i": "my-string", "j": [], "k": {}}""";
        Object jv = JsonParser.parse(source);
        assertInstanceOf(Map.class, jv);
        Map m = (Map) jv;
        assertEquals(11, m.size());
        assertEquals(0L, m.get("a"));
        assertEquals(1L, m.get("b"));
        assertEquals(-1L, m.get("c"));
        assertEquals(0.0, m.get("d"));
        assertEquals(1.0, m.get("e"));
        assertEquals(Boolean.FALSE, m.get("f"));
        assertEquals(Boolean.TRUE, m.get("g"));
        assertEquals(JsonNull.SINGLETON, m.get("h"));
        assertEquals("my-string", m.get("i"));
        assertEquals(List.of(), m.get("j"));
        assertEquals(Map.of(), m.get("k"));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testObjectEmpty() {
        Object jv = JsonParser.parse("{}");
        assertInstanceOf(Map.class, jv);
        Map m = (Map) jv;
        assertTrue(m.isEmpty());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testParseAndCast() {
        long zero = JsonParser.parseAndCast("0");
        assertEquals(0L, zero);
        long one = JsonParser.parseAndCast("1");
        assertEquals(1L, one);
        long negativeOne = JsonParser.parseAndCast("-1");
        assertEquals(-1L, negativeOne);
        double doubleZero = JsonParser.parseAndCast("0.0");
        assertEquals(0.0, doubleZero, 0.0001);
        double doubleOne = JsonParser.parseAndCast("1.0");
        assertEquals(1.0, doubleOne, 0.0001);
        boolean booleanFalse = JsonParser.parseAndCast("false");
        assertFalse(booleanFalse);
        boolean booleanTrue = JsonParser.parseAndCast("true");
        assertTrue(booleanTrue);
        Object jsonNull = JsonParser.parseAndCast("null");
        assertEquals(JsonNull.SINGLETON, jsonNull);
        String myString = JsonParser.parseAndCast("\"my-string\"");
        assertEquals("my-string", myString);
        List emptyList = JsonParser.parseAndCast("[]");
        assertEquals(List.of(), emptyList);
        Map emptyObject = JsonParser.parseAndCast("{}");
        assertEquals(Map.of(), emptyObject);
    }

}
