/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * FLT_LITERAL: DIGIT+ '.' DIGIT+ ([eE] ('+' | '-')? DIGIT+)? [fFdD]?;
 */
public class TestLexerFlts {

    @Test
    public void test01() {
        Lexer lexer;

        lexer = new Lexer("0.0");
        assertTrue(lexer.nextToken().isFlt("0.0"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0");
        assertTrue(lexer.nextToken().isFlt("1.0"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("01.0");
        assertTrue(lexer.nextToken().isFlt("01.0"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("00001.0");
        assertTrue(lexer.nextToken().isFlt("00001.0"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0.0f");
        assertTrue(lexer.nextToken().isFlt("0.0f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0f");
        assertTrue(lexer.nextToken().isFlt("1.0f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("01.0f");
        assertTrue(lexer.nextToken().isFlt("01.0f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("00001.0f");
        assertTrue(lexer.nextToken().isFlt("00001.0f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0.0F");
        assertTrue(lexer.nextToken().isFlt("0.0F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0F");
        assertTrue(lexer.nextToken().isFlt("1.0F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("01.0F");
        assertTrue(lexer.nextToken().isFlt("01.0F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("00001.0F");
        assertTrue(lexer.nextToken().isFlt("00001.0F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0.0d");
        assertTrue(lexer.nextToken().isFlt("0.0d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0d");
        assertTrue(lexer.nextToken().isFlt("1.0d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("01.0d");
        assertTrue(lexer.nextToken().isFlt("01.0d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("00001.0d");
        assertTrue(lexer.nextToken().isFlt("00001.0d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0.0D");
        assertTrue(lexer.nextToken().isFlt("0.0D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0D");
        assertTrue(lexer.nextToken().isFlt("1.0D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("01.0D");
        assertTrue(lexer.nextToken().isFlt("01.0D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("00001.0D");
        assertTrue(lexer.nextToken().isFlt("00001.0D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.12");
        assertTrue(lexer.nextToken().isFlt("123.12"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("a 123.12");
        assertTrue(lexer.nextToken().isIdent("a"));
        assertTrue(lexer.nextToken().isFlt("123.12"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("a 123.12 b");
        assertTrue(lexer.nextToken().isIdent("a"));
        assertTrue(lexer.nextToken().isFlt("123.12"));
        assertTrue(lexer.nextToken().isIdent("b"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer(".123.12.");
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isFlt("123.12"));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isEof());
    }

    @Test
    public void test02() {
        Lexer lexer;

        lexer = new Lexer("1.0e0");
        assertTrue(lexer.nextToken().isFlt("1.0e0"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e-0");
        assertTrue(lexer.nextToken().isFlt("1.0e-0"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e+0");
        assertTrue(lexer.nextToken().isFlt("1.0e+0"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e1");
        assertTrue(lexer.nextToken().isFlt("1.0e1"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e-1");
        assertTrue(lexer.nextToken().isFlt("1.0e-1"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e+1");
        assertTrue(lexer.nextToken().isFlt("1.0e+1"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e10");
        assertTrue(lexer.nextToken().isFlt("1.0e10"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e-10");
        assertTrue(lexer.nextToken().isFlt("1.0e-10"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e+10");
        assertTrue(lexer.nextToken().isFlt("1.0e+10"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer(".1.0e+10.");
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isFlt("1.0e+10"));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0e10");
        assertTrue(lexer.nextToken().isFlt("123.0e10"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0e-10");
        assertTrue(lexer.nextToken().isFlt("123.0e-10"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0e+10");
        assertTrue(lexer.nextToken().isFlt("123.0e+10"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("a 1.0e+10");
        assertTrue(lexer.nextToken().isIdent("a"));
        assertTrue(lexer.nextToken().isFlt("1.0e+10"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("a 1.0e+10 b");
        assertTrue(lexer.nextToken().isIdent("a"));
        assertTrue(lexer.nextToken().isFlt("1.0e+10"));
        assertTrue(lexer.nextToken().isIdent("b"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e+10 b");
        assertTrue(lexer.nextToken().isFlt("1.0e+10"));
        assertTrue(lexer.nextToken().isIdent("b"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer(".1.0e+10.");
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isFlt("1.0e+10"));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer(".1.0e+10.");
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isFlt("1.0e+10"));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E0");
        assertTrue(lexer.nextToken().isFlt("1.0E0"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E-0");
        assertTrue(lexer.nextToken().isFlt("1.0E-0"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E+0");
        assertTrue(lexer.nextToken().isFlt("1.0E+0"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E1");
        assertTrue(lexer.nextToken().isFlt("1.0E1"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E-1");
        assertTrue(lexer.nextToken().isFlt("1.0E-1"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E+1");
        assertTrue(lexer.nextToken().isFlt("1.0E+1"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E10");
        assertTrue(lexer.nextToken().isFlt("1.0E10"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E-10");
        assertTrue(lexer.nextToken().isFlt("1.0E-10"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E+10");
        assertTrue(lexer.nextToken().isFlt("1.0E+10"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0E10");
        assertTrue(lexer.nextToken().isFlt("123.0E10"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0E-10");
        assertTrue(lexer.nextToken().isFlt("123.0E-10"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0E+10");
        assertTrue(lexer.nextToken().isFlt("123.0E+10"));
        assertTrue(lexer.nextToken().isEof());
    }

    @Test
    public void test03() {
        Lexer lexer;

        lexer = new Lexer("1.0e0f");
        assertTrue(lexer.nextToken().isFlt("1.0e0f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e-0f");
        assertTrue(lexer.nextToken().isFlt("1.0e-0f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e+0f");
        assertTrue(lexer.nextToken().isFlt("1.0e+0f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e1f");
        assertTrue(lexer.nextToken().isFlt("1.0e1f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e-1f");
        assertTrue(lexer.nextToken().isFlt("1.0e-1f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e+1f");
        assertTrue(lexer.nextToken().isFlt("1.0e+1f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e10f");
        assertTrue(lexer.nextToken().isFlt("1.0e10f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e-10f");
        assertTrue(lexer.nextToken().isFlt("1.0e-10f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e+10f");
        assertTrue(lexer.nextToken().isFlt("1.0e+10f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer(".1.0e+10f.");
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isFlt("1.0e+10f"));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0e10f");
        assertTrue(lexer.nextToken().isFlt("123.0e10f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0e-10f");
        assertTrue(lexer.nextToken().isFlt("123.0e-10f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0e+10f");
        assertTrue(lexer.nextToken().isFlt("123.0e+10f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("a 1.0e+10f");
        assertTrue(lexer.nextToken().isIdent("a"));
        assertTrue(lexer.nextToken().isFlt("1.0e+10f"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("a 1.0e+10f b");
        assertTrue(lexer.nextToken().isIdent("a"));
        assertTrue(lexer.nextToken().isFlt("1.0e+10f"));
        assertTrue(lexer.nextToken().isIdent("b"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e+10f b");
        assertTrue(lexer.nextToken().isFlt("1.0e+10f"));
        assertTrue(lexer.nextToken().isIdent("b"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer(".1.0e+10f.");
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isFlt("1.0e+10f"));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer(".1.0e+10f.");
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isFlt("1.0e+10f"));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E0F");
        assertTrue(lexer.nextToken().isFlt("1.0E0F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E-0F");
        assertTrue(lexer.nextToken().isFlt("1.0E-0F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E+0F");
        assertTrue(lexer.nextToken().isFlt("1.0E+0F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E1F");
        assertTrue(lexer.nextToken().isFlt("1.0E1F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E-1F");
        assertTrue(lexer.nextToken().isFlt("1.0E-1F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E+1F");
        assertTrue(lexer.nextToken().isFlt("1.0E+1F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E10F");
        assertTrue(lexer.nextToken().isFlt("1.0E10F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E-10F");
        assertTrue(lexer.nextToken().isFlt("1.0E-10F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E+10F");
        assertTrue(lexer.nextToken().isFlt("1.0E+10F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0E10F");
        assertTrue(lexer.nextToken().isFlt("123.0E10F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0E-10F");
        assertTrue(lexer.nextToken().isFlt("123.0E-10F"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0E+10F");
        assertTrue(lexer.nextToken().isFlt("123.0E+10F"));
        assertTrue(lexer.nextToken().isEof());
    }

    @Test
    public void test04() {
        Lexer lexer;

        lexer = new Lexer("1.0e0d");
        assertTrue(lexer.nextToken().isFlt("1.0e0d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e-0d");
        assertTrue(lexer.nextToken().isFlt("1.0e-0d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e+0d");
        assertTrue(lexer.nextToken().isFlt("1.0e+0d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e1d");
        assertTrue(lexer.nextToken().isFlt("1.0e1d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e-1d");
        assertTrue(lexer.nextToken().isFlt("1.0e-1d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e+1d");
        assertTrue(lexer.nextToken().isFlt("1.0e+1d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e10d");
        assertTrue(lexer.nextToken().isFlt("1.0e10d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e-10d");
        assertTrue(lexer.nextToken().isFlt("1.0e-10d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e+10d");
        assertTrue(lexer.nextToken().isFlt("1.0e+10d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer(".1.0e+10d.");
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isFlt("1.0e+10d"));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0e10d");
        assertTrue(lexer.nextToken().isFlt("123.0e10d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0e-10d");
        assertTrue(lexer.nextToken().isFlt("123.0e-10d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0e+10d");
        assertTrue(lexer.nextToken().isFlt("123.0e+10d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("a 1.0e+10d");
        assertTrue(lexer.nextToken().isIdent("a"));
        assertTrue(lexer.nextToken().isFlt("1.0e+10d"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("a 1.0e+10d b");
        assertTrue(lexer.nextToken().isIdent("a"));
        assertTrue(lexer.nextToken().isFlt("1.0e+10d"));
        assertTrue(lexer.nextToken().isIdent("b"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0e+10d b");
        assertTrue(lexer.nextToken().isFlt("1.0e+10d"));
        assertTrue(lexer.nextToken().isIdent("b"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer(".1.0e+10d.");
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isFlt("1.0e+10d"));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer(".1.0e+10d.");
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isFlt("1.0e+10d"));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E0D");
        assertTrue(lexer.nextToken().isFlt("1.0E0D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E-0D");
        assertTrue(lexer.nextToken().isFlt("1.0E-0D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E+0D");
        assertTrue(lexer.nextToken().isFlt("1.0E+0D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E1D");
        assertTrue(lexer.nextToken().isFlt("1.0E1D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E-1D");
        assertTrue(lexer.nextToken().isFlt("1.0E-1D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E+1D");
        assertTrue(lexer.nextToken().isFlt("1.0E+1D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E10D");
        assertTrue(lexer.nextToken().isFlt("1.0E10D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E-10D");
        assertTrue(lexer.nextToken().isFlt("1.0E-10D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.0E+10D");
        assertTrue(lexer.nextToken().isFlt("1.0E+10D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0E10D");
        assertTrue(lexer.nextToken().isFlt("123.0E10D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0E-10D");
        assertTrue(lexer.nextToken().isFlt("123.0E-10D"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("123.0E+10D");
        assertTrue(lexer.nextToken().isFlt("123.0E+10D"));
        assertTrue(lexer.nextToken().isEof());
    }

}
