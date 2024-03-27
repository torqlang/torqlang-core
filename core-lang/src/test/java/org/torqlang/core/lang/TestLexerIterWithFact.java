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

public class TestLexerIterWithFact {

    // @formatter:off
    private static final String BLOCK_COMMENT_VALUE =
        "/*func fact_cps(n, k) in\n" +
            "            if n==0 then k\n" +
            "            else fact_cps(n - 1, n * k) end\n" +
            "        end*/";
    // @formatter:on

    private static final String FACTORIAL = """
        local fact in
            fact = func (x) in
                // Continuation Passing Style
                func fact_cps(n, k) in
                    if n==0 then k
                    else fact_cps(n - 1, n * k) end
                end
                /*func fact_cps(n, k) in
                    if n==0 then k
                    else fact_cps(n - 1, n * k) end
                end*/
                fact_cps(x, 1)
            end
            fact(100m)
        end""";

    @Test
    public void test01() {
        LexerIter lexer = new LexerIter("");
        assertNull(lexer.current());
        assertTrue(lexer.hasNext());
        assertTrue(lexer.next().isEof());
        assertNotNull(lexer.current());
        assertEquals(LexerTokenType.EOF_TOKEN, lexer.current().type());
        assertFalse(lexer.hasNext());
        assertThrows(NoSuchElementException.class, lexer::next);
    }

    @Test
    public void test02() {
        LexerIter lexer = new LexerIter(";");
        assertNull(lexer.current());
        assertTrue(lexer.hasNext());
        assertTrue(lexer.next().isOneCharSymbol(';'));
        assertNotNull(lexer.current());
        assertTrue(lexer.current().isOneCharSymbol(';'));
        assertTrue(lexer.hasNext());
        assertTrue(lexer.next().isEof());
        assertNotNull(lexer.current());
        assertTrue(lexer.current().isEof());
        assertNotNull(lexer.current());
        assertEquals(LexerTokenType.EOF_TOKEN, lexer.current().type());
        assertFalse(lexer.hasNext());
        assertThrows(NoSuchElementException.class, lexer::next);
    }

    /**
     * With comments
     */
    @Test
    public void test03() {
        LexerIter lexer = new LexerIter(FACTORIAL, false);
        assertNull(lexer.current());
        assertTrue(lexer.hasNext());
        assertTrue(lexer.next().isKeyword("local"));
        assertTrue(lexer.next().isIdent("fact"));
        assertTrue(lexer.next().isKeyword("in"));
        assertTrue(lexer.next().isIdent("fact"));
        assertTrue(lexer.next().isOneCharSymbol('='));
        assertTrue(lexer.next().isKeyword("func"));
        assertTrue(lexer.next().isOneCharSymbol('('));
        assertTrue(lexer.next().isIdent("x"));
        assertTrue(lexer.next().isOneCharSymbol(')'));
        assertTrue(lexer.next().isKeyword("in"));
        assertTrue(lexer.next().isComment("// Continuation Passing Style"));
        assertTrue(lexer.next().isKeyword("func"));
        assertTrue(lexer.next().isIdent("fact_cps"));
        assertTrue(lexer.next().isOneCharSymbol('('));
        assertTrue(lexer.next().isIdent("n"));
        assertTrue(lexer.next().isOneCharSymbol(','));
        assertTrue(lexer.next().isIdent("k"));
        assertTrue(lexer.next().isOneCharSymbol(')'));
        assertTrue(lexer.next().isKeyword("in"));
        assertTrue(lexer.next().isKeyword("if"));
        assertTrue(lexer.next().isIdent("n"));
        assertTrue(lexer.next().isTwoCharSymbol("=="));
        assertTrue(lexer.next().isInt("0"));
        assertTrue(lexer.next().isKeyword("then"));
        assertTrue(lexer.next().isIdent("k"));
        assertTrue(lexer.next().isKeyword("else"));
        assertTrue(lexer.next().isIdent("fact_cps"));
        assertTrue(lexer.next().isOneCharSymbol('('));
        assertTrue(lexer.next().isIdent("n"));
        assertTrue(lexer.next().isOneCharSymbol('-'));
        assertTrue(lexer.next().isInt("1"));
        assertTrue(lexer.next().isOneCharSymbol(','));
        assertTrue(lexer.next().isIdent("n"));
        assertTrue(lexer.next().isOneCharSymbol('*'));
        assertTrue(lexer.next().isIdent("k"));
        assertTrue(lexer.next().isOneCharSymbol(')'));
        assertTrue(lexer.next().isKeyword("end"));
        assertTrue(lexer.next().isKeyword("end"));
        assertTrue(lexer.next().isComment(BLOCK_COMMENT_VALUE));
        assertTrue(lexer.next().isIdent("fact_cps"));
        assertTrue(lexer.next().isOneCharSymbol('('));
        assertTrue(lexer.next().isIdent("x"));
        assertTrue(lexer.next().isOneCharSymbol(','));
        assertTrue(lexer.next().isInt("1"));
        assertTrue(lexer.next().isOneCharSymbol(')'));
        assertTrue(lexer.next().isKeyword("end"));
        assertTrue(lexer.next().isIdent("fact"));
        assertTrue(lexer.next().isOneCharSymbol('('));
        assertTrue(lexer.next().isDec("100m"));
        assertTrue(lexer.next().isOneCharSymbol(')'));
        assertTrue(lexer.next().isKeyword("end"));
        assertTrue(lexer.next().isEof());
        assertNotNull(lexer.current());
        assertEquals(LexerTokenType.EOF_TOKEN, lexer.current().type());
        assertFalse(lexer.hasNext());
        assertThrows(NoSuchElementException.class, lexer::next);
    }


    /**
     * Without comments
     */
    @Test
    public void test04() {
        LexerIter lexer = new LexerIter(FACTORIAL);
        assertNull(lexer.current());
        assertTrue(lexer.hasNext());
        assertTrue(lexer.next().isKeyword("local"));
        assertTrue(lexer.next().isIdent("fact"));
        assertTrue(lexer.next().isKeyword("in"));
        assertTrue(lexer.next().isIdent("fact"));
        assertTrue(lexer.next().isOneCharSymbol('='));
        assertTrue(lexer.next().isKeyword("func"));
        assertTrue(lexer.next().isOneCharSymbol('('));
        assertTrue(lexer.next().isIdent("x"));
        assertTrue(lexer.next().isOneCharSymbol(')'));
        assertTrue(lexer.next().isKeyword("in"));
        assertTrue(lexer.next().isKeyword("func"));
        assertTrue(lexer.next().isIdent("fact_cps"));
        assertTrue(lexer.next().isOneCharSymbol('('));
        assertTrue(lexer.next().isIdent("n"));
        assertTrue(lexer.next().isOneCharSymbol(','));
        assertTrue(lexer.next().isIdent("k"));
        assertTrue(lexer.next().isOneCharSymbol(')'));
        assertTrue(lexer.next().isKeyword("in"));
        assertTrue(lexer.next().isKeyword("if"));
        assertTrue(lexer.next().isIdent("n"));
        assertTrue(lexer.next().isTwoCharSymbol("=="));
        assertTrue(lexer.next().isInt("0"));
        assertTrue(lexer.next().isKeyword("then"));
        assertTrue(lexer.next().isIdent("k"));
        assertTrue(lexer.next().isKeyword("else"));
        assertTrue(lexer.next().isIdent("fact_cps"));
        assertTrue(lexer.next().isOneCharSymbol('('));
        assertTrue(lexer.next().isIdent("n"));
        assertTrue(lexer.next().isOneCharSymbol('-'));
        assertTrue(lexer.next().isInt("1"));
        assertTrue(lexer.next().isOneCharSymbol(','));
        assertTrue(lexer.next().isIdent("n"));
        assertTrue(lexer.next().isOneCharSymbol('*'));
        assertTrue(lexer.next().isIdent("k"));
        assertTrue(lexer.next().isOneCharSymbol(')'));
        assertTrue(lexer.next().isKeyword("end"));
        assertTrue(lexer.next().isKeyword("end"));
        assertTrue(lexer.next().isIdent("fact_cps"));
        assertTrue(lexer.next().isOneCharSymbol('('));
        assertTrue(lexer.next().isIdent("x"));
        assertTrue(lexer.next().isOneCharSymbol(','));
        assertTrue(lexer.next().isInt("1"));
        assertTrue(lexer.next().isOneCharSymbol(')'));
        assertTrue(lexer.next().isKeyword("end"));
        assertTrue(lexer.next().isIdent("fact"));
        assertTrue(lexer.next().isOneCharSymbol('('));
        assertTrue(lexer.next().isDec("100m"));
        assertTrue(lexer.next().isOneCharSymbol(')'));
        assertTrue(lexer.next().isKeyword("end"));
        assertTrue(lexer.next().isEof());
        assertNotNull(lexer.current());
        assertEquals(LexerTokenType.EOF_TOKEN, lexer.current().type());
        assertFalse(lexer.hasNext());
        assertThrows(NoSuchElementException.class, lexer::next);
    }

}
