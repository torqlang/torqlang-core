/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.torqlang.core.lang.JsonLexerTokenType.*;

public class TestJsonLexer {

    @Test
    public void test() {
        JsonLexerToken next;
        //            1         2         3         4
        //  01234567890123456789012345678901234567890123
        String source = """
            [0, -1, 1.0, false, true, null, "my-string"]""";
        JsonLexer lexer = new JsonLexer(source);

        assertEquals(0, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.substringEquals("["));
        assertEquals(new JsonLexerToken(DELIMITER, source, 0, 1), next);

        assertEquals(1, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.substringEquals("0"));
        assertEquals(new JsonLexerToken(NUMBER, source, 1, 2), next);

        assertEquals(2, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.substringEquals(","));
        assertEquals(new JsonLexerToken(DELIMITER, source, 2, 3), next);

        assertEquals(3, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.substringEquals("-1"));
        assertEquals(new JsonLexerToken(NUMBER, source, 4, 6), next);

        assertEquals(6, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.substringEquals(","));
        assertEquals(new JsonLexerToken(DELIMITER, source, 6, 7), next);

        assertEquals(7, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.substringEquals("1.0"));
        assertEquals(new JsonLexerToken(NUMBER, source, 8, 11), next);

        assertEquals(11, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.substringEquals(","));
        assertEquals(new JsonLexerToken(DELIMITER, source, 11, 12), next);

        assertEquals(12, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.substringEquals("false"));
        assertEquals(new JsonLexerToken(BOOLEAN, source, 13, 18), next);

        assertEquals(18, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.substringEquals(","));
        assertEquals(new JsonLexerToken(DELIMITER, source, 18, 19), next);

        assertEquals(19, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.substringEquals("true"));
        assertEquals(new JsonLexerToken(BOOLEAN, source, 20, 24), next);

        assertEquals(24, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.substringEquals(","));
        assertEquals(new JsonLexerToken(DELIMITER, source, 24, 25), next);

        assertEquals(25, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.substringEquals("null"));
        assertEquals(new JsonLexerToken(NULL, source, 26, 30), next);

        assertEquals(30, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.substringEquals(","));
        assertEquals(new JsonLexerToken(DELIMITER, source, 30, 31), next);

        assertEquals(31, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.substringEquals("\"my-string\""));
        assertEquals(new JsonLexerToken(STRING, source, 32, 43), next);

        assertEquals(43, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.substringEquals("]"));
        assertEquals(new JsonLexerToken(DELIMITER, source, 43, 44), next);

        assertEquals(44, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.substringEquals(""));
        assertEquals(EOF, next.type());
        assertEquals(new JsonLexerToken(EOF, source, 44, 44), next);
        {
            NoSuchElementException exc = assertThrows(NoSuchElementException.class, next::firstChar);
            assertEquals("44", exc.getMessage());
        }
        {
            final JsonLexerToken next2 = next;
            NoSuchElementException exc = assertThrows(NoSuchElementException.class, () -> next2.substringCharAt(0));
            assertEquals("44", exc.getMessage());
        }
    }

    @Test
    public void testErrors() {
        {
            JsonLexer lexer = new JsonLexer("1x");
            Exception exc = assertThrows(JsonLexerException.class, lexer::nextToken);
            assertEquals("Invalid number expression", exc.getMessage());
        }
        {
            JsonLexer lexer = new JsonLexer("1.");
            Exception exc = assertThrows(JsonLexerException.class, lexer::nextToken);
            assertEquals("Invalid floating point expression", exc.getMessage());
        }
        {
            JsonLexer lexer = new JsonLexer("1.a");
            Exception exc = assertThrows(JsonLexerException.class, lexer::nextToken);
            assertEquals("Invalid floating point expression", exc.getMessage());
        }
        {
            JsonLexer lexer = new JsonLexer("1.0a");
            Exception exc = assertThrows(JsonLexerException.class, lexer::nextToken);
            assertEquals("Invalid number expression", exc.getMessage());
        }
        {
            JsonLexer lexer = new JsonLexer("true_");
            Exception exc = assertThrows(JsonLexerException.class, lexer::nextToken);
            assertEquals("Invalid boolean expression", exc.getMessage());
        }
        {
            JsonLexer lexer = new JsonLexer("false_");
            Exception exc = assertThrows(JsonLexerException.class, lexer::nextToken);
            assertEquals("Invalid boolean expression", exc.getMessage());
        }
        {
            JsonLexer lexer = new JsonLexer("null_");
            Exception exc = assertThrows(JsonLexerException.class, lexer::nextToken);
            assertEquals("Invalid null expression", exc.getMessage());
        }
        {
            JsonLexer lexer = new JsonLexer("\"oops");
            Exception exc = assertThrows(JsonLexerException.class, lexer::nextToken);
            assertEquals("String is missing closing quote", exc.getMessage());
        }
    }

    @Test
    public void testHashcode() {

        JsonLexerToken token1 = new JsonLexerToken(STRING, "[0, 1]", 0, 6);
        assertTrue(token1.substringEquals("[0, 1]"));

        JsonLexerToken token2 = new JsonLexerToken(STRING, "[true, [0, 1], false]", 7, 13);
        assertTrue(token2.substringEquals("[0, 1]"));
        assertEquals("[true, [0, 1], false]", token2.source());
        assertEquals(7, token2.begin());
        assertEquals(13, token2.end());
        assertEquals('[', token2.substringCharAt(0));
        assertEquals('0', token2.substringCharAt(1));
        assertEquals(',', token2.substringCharAt(2));
        assertEquals(' ', token2.substringCharAt(3));
        assertEquals('1', token2.substringCharAt(4));
        assertEquals(']', token2.substringCharAt(5));

        assertEquals(token1.hashCode(), token2.hashCode());
        int cachedHash = token1.hashCode();
        assertEquals(cachedHash, token1.hashCode());
    }

    @Test
    public void testNotEquals() {
        JsonLexerToken token1 = new JsonLexerToken(STRING, "[0, 1]", 0, 6);
        JsonLexerToken token2 = new JsonLexerToken(STRING, "[1, 2]", 0, 6);
        assertNotEquals(token1, token2);

        assertTrue(token1.substringEquals("[0, 1]"));
        assertFalse(token1.substringEquals("[1, 2]"));
        assertFalse(token1.substringEquals("[10, 20]"));

        assertTrue(token1.firstCharEquals('['));
        assertEquals('[', token1.firstChar());
        assertFalse(token1.firstCharEquals(']'));
    }

    @Test
    public void testWhitespace() {
        JsonLexerToken next;
        String source = "[0,\n1,\r2,\t3,\f4]";
        JsonLexer lexer = new JsonLexer(source);
        next = lexer.nextToken();
        assertTrue(next.substringEquals("["));
        next = lexer.nextToken();
        assertTrue(next.substringEquals("0"));
        next = lexer.nextToken();
        assertTrue(next.substringEquals(","));
        next = lexer.nextToken();
        assertTrue(next.substringEquals("1"));
        next = lexer.nextToken();
        assertTrue(next.substringEquals(","));
        next = lexer.nextToken();
        assertTrue(next.substringEquals("2"));
        next = lexer.nextToken();
        assertTrue(next.substringEquals(","));
        next = lexer.nextToken();
        assertTrue(next.substringEquals("3"));
        next = lexer.nextToken();
        assertTrue(next.substringEquals(","));
        next = lexer.nextToken();
        assertTrue(next.substringEquals("4"));
        next = lexer.nextToken();
        assertTrue(next.substringEquals("]"));
    }

}
