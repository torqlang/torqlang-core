/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.torqlang.core.lang.LexerTokenType.*;

public class TestLexer {

    @Test
    public void test() {
        LexerToken next;
        //            1         2         3         4         5         6
        //  0123456789012345678901234567890123456789012345678901234567890
        String source = """
            [0, -1, 1.0, 1.23m, false, true, test, "my-string", ==, ...]""";
        Lexer lexer = new Lexer(source);
        assertEquals(source, lexer.source());

        assertEquals(0, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.isOneCharSymbol());
        assertTrue(next.isOneCharSymbol('['));
        assertTrue(next.substringEquals("["));
        assertEquals(new LexerToken(ONE_CHAR_TOKEN, source, 0, 1), next);
        assertEquals("[0, 1] ONE_CHAR_TOKEN: [", next.toString());

        assertEquals(1, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.isInt());
        assertTrue(next.isInt("0"));
        assertTrue(next.substringEquals("0"));
        assertEquals(new LexerToken(INT_TOKEN, source, 1, 2), next);

        assertEquals(2, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.isOneCharSymbol());
        assertTrue(next.isOneCharSymbol(','));
        assertTrue(next.substringEquals(","));
        assertEquals(new LexerToken(ONE_CHAR_TOKEN, source, 2, 3), next);

        assertEquals(3, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.isOneCharSymbol());
        assertTrue(next.isOneCharSymbol('-'));
        assertTrue(next.substringEquals("-"));
        assertEquals(new LexerToken(ONE_CHAR_TOKEN, source, 4, 5), next);

        assertEquals(5, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.isInt());
        assertTrue(next.isInt("1"));
        assertTrue(next.substringEquals("1"));
        assertEquals(new LexerToken(INT_TOKEN, source, 5, 6), next);

        assertEquals(6, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.isOneCharSymbol());
        assertTrue(next.isOneCharSymbol(','));
        assertTrue(next.substringEquals(","));
        assertEquals(new LexerToken(ONE_CHAR_TOKEN, source, 6, 7), next);

        assertEquals(7, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.substringEquals("1.0"));
        assertEquals(new LexerToken(FLT_TOKEN, source, 8, 11), next);

        assertEquals(11, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.isOneCharSymbol());
        assertTrue(next.isOneCharSymbol(','));
        assertTrue(next.substringEquals(","));
        assertEquals(new LexerToken(ONE_CHAR_TOKEN, source, 11, 12), next);

        assertEquals(12, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.isDec());
        assertTrue(next.isDec("1.23m"));
        assertTrue(next.substringEquals("1.23m"));
        assertEquals(new LexerToken(DEC_TOKEN, source, 13, 18), next);

        assertEquals(18, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.isOneCharSymbol());
        assertTrue(next.isOneCharSymbol(','));
        assertTrue(next.substringEquals(","));
        assertEquals(new LexerToken(ONE_CHAR_TOKEN, source, 18, 19), next);

        assertEquals(19, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.substringEquals("false"));
        assertEquals(new LexerToken(KEYWORD_TOKEN, source, 20, 25), next);

        assertEquals(25, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.isOneCharSymbol());
        assertTrue(next.isOneCharSymbol(','));
        assertTrue(next.substringEquals(","));
        assertEquals(new LexerToken(ONE_CHAR_TOKEN, source, 25, 26), next);

        assertEquals(26, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.substringEquals("true"));
        assertEquals(new LexerToken(KEYWORD_TOKEN, source, 27, 31), next);

        assertEquals(31, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.isOneCharSymbol());
        assertTrue(next.isOneCharSymbol(','));
        assertTrue(next.substringEquals(","));
        assertEquals(new LexerToken(ONE_CHAR_TOKEN, source, 31, 32), next);

        assertEquals(32, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.isIdent());
        assertTrue(next.isIdent("test"));
        assertTrue(next.substringEquals("test"));
        assertEquals(new LexerToken(IDENT_TOKEN, source, 33, 37), next);

        assertEquals(37, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.isOneCharSymbol());
        assertTrue(next.isOneCharSymbol(','));
        assertTrue(next.substringEquals(","));
        assertEquals(new LexerToken(ONE_CHAR_TOKEN, source, 37, 38), next);

        assertEquals(38, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.isStr());
        assertTrue(next.isStr("\"my-string\""));
        assertTrue(next.substringEquals("\"my-string\""));
        assertEquals(new LexerToken(STR_TOKEN, source, 39, 50), next);

        assertEquals(50, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.isOneCharSymbol());
        assertTrue(next.isOneCharSymbol(','));
        assertTrue(next.substringEquals(","));
        assertEquals(new LexerToken(ONE_CHAR_TOKEN, source, 50, 51), next);

        assertEquals(51, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.isTwoCharSymbol());
        assertTrue(next.isTwoCharSymbol("=="));
        assertTrue(next.substringEquals("=="));
        assertEquals(new LexerToken(TWO_CHAR_TOKEN, source, 52, 54), next);

        assertEquals(54, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.isOneCharSymbol());
        assertTrue(next.isOneCharSymbol(','));
        assertTrue(next.substringEquals(","));
        assertEquals(new LexerToken(ONE_CHAR_TOKEN, source, 54, 55), next);

        assertEquals(55, lexer.charPos()); // We are on a space that will be skipped
        next = lexer.nextToken();
        assertTrue(next.isThreeCharSymbol());
        assertTrue(next.isThreeCharSymbol("..."));
        assertTrue(next.substringEquals("..."));
        assertEquals(new LexerToken(THREE_CHAR_TOKEN, source, 56, 59), next);

        assertEquals(59, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.isOneCharSymbol());
        assertTrue(next.isOneCharSymbol(']'));
        assertTrue(next.substringEquals("]"));
        assertEquals(new LexerToken(ONE_CHAR_TOKEN, source, 59, 60), next);

        assertEquals(60, lexer.charPos());
        next = lexer.nextToken();
        assertTrue(next.isEof());
        assertTrue(next.substringEquals(""));
        assertEquals(EOF_TOKEN, next.type());
        assertEquals(new LexerToken(EOF_TOKEN, source, 60, 60), next);
        {
            NoSuchElementException exc = assertThrows(NoSuchElementException.class, next::firstChar);
            assertEquals("60", exc.getMessage());
        }
        {
            final LexerToken next2 = next;
            NoSuchElementException exc = assertThrows(NoSuchElementException.class, () -> next2.substringCharAt(0));
            assertEquals("60", exc.getMessage());
        }
    }

    @Test
    public void testErrors() {
        {
            String source = "1x";
            Lexer lexer = new Lexer(source);
            LexerError exc = assertThrows(LexerError.class, lexer::nextToken);
            assertEquals(new LexerToken(INT_TOKEN, source, 0, 2), exc.token);
            assertEquals("Invalid integer", exc.getMessage());
        }
        {
            String source = "1.";
            Lexer lexer = new Lexer(source);
            LexerError exc = assertThrows(LexerError.class, lexer::nextToken);
            assertEquals(new LexerToken(FLT_TOKEN, source, 0, 2), exc.token);
            assertEquals("Invalid floating point number", exc.getMessage());
        }
        {
            String source = "1.a";
            Lexer lexer = new Lexer(source);
            LexerError exc = assertThrows(LexerError.class, lexer::nextToken);
            assertEquals(new LexerToken(FLT_TOKEN, source, 0, 3), exc.token);
            assertEquals("Invalid floating point number", exc.getMessage());
        }
        {
            String source = "1.0a";
            Lexer lexer = new Lexer(source);
            LexerError exc = assertThrows(LexerError.class, lexer::nextToken);
            assertEquals(new LexerToken(FLT_TOKEN, source, 0, 4), exc.token);
            assertEquals("Floating point suffix must be one of [fFdDmM]", exc.getMessage());
        }
        {
            String source = "1.0fa";
            Lexer lexer = new Lexer(source);
            LexerError exc = assertThrows(LexerError.class, lexer::nextToken);
            assertEquals(new LexerToken(FLT_TOKEN, source, 0, 5), exc.token);
            assertEquals("Invalid floating point number", exc.getMessage());
        }
        {
            String source = "1.0ma";
            Lexer lexer = new Lexer(source);
            LexerError exc = assertThrows(LexerError.class, lexer::nextToken);
            assertEquals(new LexerToken(DEC_TOKEN, source, 0, 5), exc.token);
            assertEquals("Invalid decimal number", exc.getMessage());
        }
        {
            String source = "\"oops";
            Lexer lexer = new Lexer(source);
            LexerError exc = assertThrows(LexerError.class, lexer::nextToken);
            assertEquals(new LexerToken(STR_TOKEN, source, 0, 5), exc.token);
            assertEquals("String is missing closing double quote", exc.getMessage());
        }
    }

    @Test
    public void testEscSeq() {
        /*
         * Precondition: Current charPos is the character following the escape '\\'
         * fragment ESC_SEQ: '\\' ([tbnrf'"`\\] | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT);
         */
    }

    @Test
    public void testHashcode() {

        LexerToken token1 = new LexerToken(STR_TOKEN, "[0, 1]", 0, 6);
        assertTrue(token1.substringEquals("[0, 1]"));

        LexerToken token2 = new LexerToken(STR_TOKEN, "[true, [0, 1], false]", 7, 13);
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
        LexerToken token1 = new LexerToken(STR_TOKEN, "[0, 1]", 0, 6);
        LexerToken token2 = new LexerToken(STR_TOKEN, "[1, 2]", 0, 6);
        assertNotEquals(token1, token2);

        assertTrue(token1.substringEquals("[0, 1]"));
        assertFalse(token1.substringEquals("[1, 2]"));

        assertTrue(token1.firstCharEquals('['));
        assertEquals('[', token1.firstChar());
        assertFalse(token1.firstCharEquals(']'));
    }

    @Test
    public void testWhitespace() {
        LexerToken next;
        String source = "[0,\n1,\r2,\t3,\f4]";
        Lexer lexer = new Lexer(source);
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
