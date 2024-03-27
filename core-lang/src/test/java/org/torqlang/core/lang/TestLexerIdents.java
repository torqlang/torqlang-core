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
 * IDENT: ((ALPHA | '_') (ALPHA_NUMERIC | '_')*) | '`' (~('`' | '\\') | ESC_SEQ)+ '`';
 * fragment ESC_SEQ: '\\' ([tbnrf'"`\\] | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT);
 */
public class TestLexerIdents {

    @Test
    public void test01() {
        Lexer lexer;

        lexer = new Lexer("a");
        assertTrue(lexer.nextToken().isIdent("a"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("_");
        assertTrue(lexer.nextToken().isIdent("_"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("_a");
        assertTrue(lexer.nextToken().isIdent("_a"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("a_");
        assertTrue(lexer.nextToken().isIdent("a_"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("a_b");
        assertTrue(lexer.nextToken().isIdent("a_b"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("A");
        assertTrue(lexer.nextToken().isIdent("A"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("_");
        assertTrue(lexer.nextToken().isIdent("_"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("_A");
        assertTrue(lexer.nextToken().isIdent("_A"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("A_");
        assertTrue(lexer.nextToken().isIdent("A_"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("A_B");
        assertTrue(lexer.nextToken().isIdent("A_B"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("`this is an ident`");
        assertTrue(lexer.nextToken().isIdent("`this is an ident`"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("`this is \\' an ident`");
        assertTrue(lexer.nextToken().isIdent("`this is \\' an ident`"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("`this is \\\" an ident`");
        assertTrue(lexer.nextToken().isIdent("`this is \\\" an ident`"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("`this is \\` an ident`");
        assertTrue(lexer.nextToken().isIdent("`this is \\` an ident`"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("`this is \\t an ident`");
        assertTrue(lexer.nextToken().isIdent("`this is \\t an ident`"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("`this is \\b an ident`");
        assertTrue(lexer.nextToken().isIdent("`this is \\b an ident`"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("`this is \\n an ident`");
        assertTrue(lexer.nextToken().isIdent("`this is \\n an ident`"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("`this is \\r an ident`");
        assertTrue(lexer.nextToken().isIdent("`this is \\r an ident`"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("`this is \\f an ident`");
        assertTrue(lexer.nextToken().isIdent("`this is \\f an ident`"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("`this is \\\\ an ident`");
        assertTrue(lexer.nextToken().isIdent("`this is \\\\ an ident`"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("a`this is \\\\ an ident`");
        assertTrue(lexer.nextToken().isIdent("a"));
        assertTrue(lexer.nextToken().isIdent("`this is \\\\ an ident`"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("`this is \\\\ an ident`b");
        assertTrue(lexer.nextToken().isIdent("`this is \\\\ an ident`"));
        assertTrue(lexer.nextToken().isIdent("b"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("a`this is \\\\ an ident`b");
        assertTrue(lexer.nextToken().isIdent("a"));
        assertTrue(lexer.nextToken().isIdent("`this is \\\\ an ident`"));
        assertTrue(lexer.nextToken().isIdent("b"));
        assertTrue(lexer.nextToken().isEof());
    }

    @Test
    public void test02() {
        Lexer lexer;
        Exception exc;

        lexer = new Lexer("`");
        exc = assertThrows(LexerError.class, lexer::nextToken);
        assertEquals("Identifier is missing closing backtick", exc.getMessage());

        // Unterminated identifier ending unterminated escape sequence
        lexer = new Lexer("`\\");
        exc = assertThrows(LexerError.class, lexer::nextToken);
        assertEquals("Identifier is missing closing backtick", exc.getMessage());
    }

}
