/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestLexerStrs {

    @Test
    public void test01() {
        Lexer lexer;

        lexer = new Lexer("''");
        assertTrue(lexer.nextToken().isStr("''"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("'a'");
        assertTrue(lexer.nextToken().isStr("'a'"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("'This is a string'");
        assertTrue(lexer.nextToken().isStr("'This is a string'"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("'This is \\t a string'");
        assertTrue(lexer.nextToken().isStr("'This is \\t a string'"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("'This is \\b a string'");
        assertTrue(lexer.nextToken().isStr("'This is \\b a string'"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("'This is \\n a string'");
        assertTrue(lexer.nextToken().isStr("'This is \\n a string'"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("'This is \\r a string'");
        assertTrue(lexer.nextToken().isStr("'This is \\r a string'"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("'This is \\f a string'");
        assertTrue(lexer.nextToken().isStr("'This is \\f a string'"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("'This is \\' a string'");
        assertTrue(lexer.nextToken().isStr("'This is \\' a string'"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("'This is \\\" a string'");
        assertTrue(lexer.nextToken().isStr("'This is \\\" a string'"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("'This is \\` a string'");
        assertTrue(lexer.nextToken().isStr("'This is \\` a string'"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("'This is \\\\ a string'");
        assertTrue(lexer.nextToken().isStr("'This is \\\\ a string'"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("'This is \\\\ \\u0000 a string'");
        assertTrue(lexer.nextToken().isStr("'This is \\\\ \\u0000 a string'"));
        assertTrue(lexer.nextToken().isEof());
    }

    @Test
    public void test02() {
        Lexer lexer;

        lexer = new Lexer("\"\"");
        assertTrue(lexer.nextToken().isStr("\"\""));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("\"a\"");
        assertTrue(lexer.nextToken().isStr("\"a\""));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("\"This is a string\"");
        assertTrue(lexer.nextToken().isStr("\"This is a string\""));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("\"This is \\t a string\"");
        assertTrue(lexer.nextToken().isStr("\"This is \\t a string\""));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("\"This is \\b a string\"");
        assertTrue(lexer.nextToken().isStr("\"This is \\b a string\""));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("\"This is \\n a string\"");
        assertTrue(lexer.nextToken().isStr("\"This is \\n a string\""));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("\"This is \\r a string\"");
        assertTrue(lexer.nextToken().isStr("\"This is \\r a string\""));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("\"This is \\f a string\"");
        assertTrue(lexer.nextToken().isStr("\"This is \\f a string\""));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("\"This is ' a string\"");
        assertTrue(lexer.nextToken().isStr("\"This is ' a string\""));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("\"This is \\\" a string\"");
        assertTrue(lexer.nextToken().isStr("\"This is \\\" a string\""));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("\"This is \\` a string\"");
        assertTrue(lexer.nextToken().isStr("\"This is \\` a string\""));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("\"This is \\\\ a string\"");
        assertTrue(lexer.nextToken().isStr("\"This is \\\\ a string\""));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("\"This is \\\\ \\u0000 a string\"");
        assertTrue(lexer.nextToken().isStr("\"This is \\\\ \\u0000 a string\""));
        assertTrue(lexer.nextToken().isEof());
    }

    @Test
    public void test03() {
        Lexer lexer;
        Exception exc;

        lexer = new Lexer("'");
        exc = assertThrows(LexerError.class, lexer::nextToken);
        assertEquals("String is missing closing single quote", exc.getMessage());

        lexer = new Lexer("\"");
        exc = assertThrows(LexerError.class, lexer::nextToken);
        assertEquals("String is missing closing double quote", exc.getMessage());

        // Unterminated string ending unterminated escape sequence
        lexer = new Lexer("'\\");
        exc = assertThrows(LexerError.class, lexer::nextToken);
        assertEquals("String is missing closing single quote", exc.getMessage());

        // A lone backslash
        lexer = new Lexer("\\");
        exc = assertThrows(LexerError.class, lexer::nextToken);
        assertEquals("Invalid token", exc.getMessage());
    }

}
