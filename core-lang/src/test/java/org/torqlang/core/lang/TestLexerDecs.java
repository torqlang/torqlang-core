/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DEC_LITERAL: DIGIT+ ('.' DIGIT+)? [mM]?;
 */
public class TestLexerDecs {

    @Test
    public void test01() {
        Lexer lexer;

        lexer = new Lexer("0m");
        assertTrue(lexer.nextToken().isDec("0m"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0.0m");
        assertTrue(lexer.nextToken().isDec("0.0m"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1m");
        assertTrue(lexer.nextToken().isDec("1m"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.1m");
        assertTrue(lexer.nextToken().isDec("1.1m"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("12345678901234567890234567890m");
        assertTrue(lexer.nextToken().isDec("12345678901234567890234567890m"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("12345678901234567890234567890.12345678901234567890234567890m");
        assertTrue(lexer.nextToken().isDec("12345678901234567890234567890.12345678901234567890234567890m"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer(".0m");
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isDec("0m"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0m.");
        assertTrue(lexer.nextToken().isDec("0m"));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer(".0m.");
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isDec("0m"));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0M");
        assertTrue(lexer.nextToken().isDec("0M"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0.0M");
        assertTrue(lexer.nextToken().isDec("0.0M"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1M");
        assertTrue(lexer.nextToken().isDec("1M"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("1.1M");
        assertTrue(lexer.nextToken().isDec("1.1M"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("12345678901234567890234567890M");
        assertTrue(lexer.nextToken().isDec("12345678901234567890234567890M"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("12345678901234567890234567890.12345678901234567890234567890M");
        assertTrue(lexer.nextToken().isDec("12345678901234567890234567890.12345678901234567890234567890M"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer(".0M");
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isDec("0M"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0M.");
        assertTrue(lexer.nextToken().isDec("0M"));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer(".0M.");
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isDec("0M"));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isEof());
    }

}
