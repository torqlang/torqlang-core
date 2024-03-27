/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * INT_LITERAL: DIGIT+ [lL]? | ('0x' | '0X') (HEX_DIGIT HEX_DIGIT)+ [lL]?;
 */
public class TestLexerInts {

    @Test
    public void test01() {
        Lexer lexer;

        lexer = new Lexer("0");
        assertTrue(lexer.nextToken().isInt("0"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1");
        assertTrue(lexer.nextToken().isInt("1"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("01");
        assertTrue(lexer.nextToken().isInt("01"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0001");
        assertTrue(lexer.nextToken().isInt("0001"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("12");
        assertTrue(lexer.nextToken().isInt("12"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123");
        assertTrue(lexer.nextToken().isInt("123"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0l");
        assertTrue(lexer.nextToken().isInt("0l"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1l");
        assertTrue(lexer.nextToken().isInt("1l"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("01l");
        assertTrue(lexer.nextToken().isInt("01l"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0001l");
        assertTrue(lexer.nextToken().isInt("0001l"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("12l");
        assertTrue(lexer.nextToken().isInt("12l"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123l");
        assertTrue(lexer.nextToken().isInt("123l"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0L");
        assertTrue(lexer.nextToken().isInt("0L"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1L");
        assertTrue(lexer.nextToken().isInt("1L"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("01L");
        assertTrue(lexer.nextToken().isInt("01L"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0001L");
        assertTrue(lexer.nextToken().isInt("0001L"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("12L");
        assertTrue(lexer.nextToken().isInt("12L"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123L");
        assertTrue(lexer.nextToken().isInt("123L"));
        assertTrue(lexer.nextToken().isEof());
    }

    @Test
    public void test02() {
        Lexer lexer;

        lexer = new Lexer("0x0");
        assertTrue(lexer.nextToken().isInt("0x0"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x1");
        assertTrue(lexer.nextToken().isInt("0x1"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x01");
        assertTrue(lexer.nextToken().isInt("0x01"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x0001");
        assertTrue(lexer.nextToken().isInt("0x0001"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x12");
        assertTrue(lexer.nextToken().isInt("0x12"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x123");
        assertTrue(lexer.nextToken().isInt("0x123"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x0l");
        assertTrue(lexer.nextToken().isInt("0x0l"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x1l");
        assertTrue(lexer.nextToken().isInt("0x1l"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x01l");
        assertTrue(lexer.nextToken().isInt("0x01l"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x0001l");
        assertTrue(lexer.nextToken().isInt("0x0001l"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x12l");
        assertTrue(lexer.nextToken().isInt("0x12l"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x123l");
        assertTrue(lexer.nextToken().isInt("0x123l"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x0L");
        assertTrue(lexer.nextToken().isInt("0x0L"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x1L");
        assertTrue(lexer.nextToken().isInt("0x1L"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x01L");
        assertTrue(lexer.nextToken().isInt("0x01L"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x0001L");
        assertTrue(lexer.nextToken().isInt("0x0001L"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x12L");
        assertTrue(lexer.nextToken().isInt("0x12L"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0x123L");
        assertTrue(lexer.nextToken().isInt("0x123L"));
        assertTrue(lexer.nextToken().isEof());
    }

    @Test
    public void test03() {
        Lexer lexer;
        Exception exc;

        lexer = new Lexer("0x");
        exc = assertThrows(LexerError.class, lexer::nextToken);
        assertEquals("Invalid hexadecimal number", exc.getMessage());

        lexer = new Lexer("0x 0");
        exc = assertThrows(LexerError.class, lexer::nextToken);
        assertEquals("Invalid hexadecimal number", exc.getMessage());

        lexer = new Lexer("0 0x 0");
        assertTrue(lexer.nextToken().isInt("0"));
        exc = assertThrows(LexerError.class, lexer::nextToken);
        assertEquals("Invalid hexadecimal number", exc.getMessage());

        lexer = new Lexer("0xG");
        exc = assertThrows(LexerError.class, lexer::nextToken);
        assertEquals("Invalid hexadecimal number", exc.getMessage());
    }

}
