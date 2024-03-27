/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestLexerWithFact {

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

    /**
     * With comments
     */
    @Test
    public void test01() {
        Lexer lexer = new Lexer(FACTORIAL);
        assertTrue(lexer.nextToken(false).isKeyword("local"));
        assertTrue(lexer.nextToken(false).isIdent("fact"));
        assertTrue(lexer.nextToken(false).isKeyword("in"));
        assertTrue(lexer.nextToken(false).isIdent("fact"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol('='));
        assertTrue(lexer.nextToken(false).isKeyword("func"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol('('));
        assertTrue(lexer.nextToken(false).isIdent("x"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol(')'));
        assertTrue(lexer.nextToken(false).isKeyword("in"));
        assertTrue(lexer.nextToken(false).isComment("// Continuation Passing Style"));
        assertTrue(lexer.nextToken(false).isKeyword("func"));
        assertTrue(lexer.nextToken(false).isIdent("fact_cps"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol('('));
        assertTrue(lexer.nextToken(false).isIdent("n"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol(','));
        assertTrue(lexer.nextToken(false).isIdent("k"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol(')'));
        assertTrue(lexer.nextToken(false).isKeyword("in"));
        assertTrue(lexer.nextToken(false).isKeyword("if"));
        assertTrue(lexer.nextToken(false).isIdent("n"));
        assertTrue(lexer.nextToken(false).isTwoCharSymbol("=="));
        assertTrue(lexer.nextToken(false).isInt("0"));
        assertTrue(lexer.nextToken(false).isKeyword("then"));
        assertTrue(lexer.nextToken(false).isIdent("k"));
        assertTrue(lexer.nextToken(false).isKeyword("else"));
        assertTrue(lexer.nextToken(false).isIdent("fact_cps"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol('('));
        assertTrue(lexer.nextToken(false).isIdent("n"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol('-'));
        assertTrue(lexer.nextToken(false).isInt("1"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol(','));
        assertTrue(lexer.nextToken(false).isIdent("n"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol('*'));
        assertTrue(lexer.nextToken(false).isIdent("k"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol(')'));
        assertTrue(lexer.nextToken(false).isKeyword("end"));
        assertTrue(lexer.nextToken(false).isKeyword("end"));
        assertTrue(lexer.nextToken(false).isComment(BLOCK_COMMENT_VALUE));
        assertTrue(lexer.nextToken(false).isIdent("fact_cps"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol('('));
        assertTrue(lexer.nextToken(false).isIdent("x"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol(','));
        assertTrue(lexer.nextToken(false).isInt("1"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol(')'));
        assertTrue(lexer.nextToken(false).isKeyword("end"));
        assertTrue(lexer.nextToken(false).isIdent("fact"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol('('));
        assertTrue(lexer.nextToken(false).isDec("100m"));
        assertTrue(lexer.nextToken(false).isOneCharSymbol(')'));
        assertTrue(lexer.nextToken(false).isKeyword("end"));
        assertTrue(lexer.nextToken(false).isEof());
    }

    /**
     * Without comments
     */
    @Test
    public void test02() {
        Lexer lexer = new Lexer(FACTORIAL);
        assertTrue(lexer.nextToken(true).isKeyword("local"));
        assertTrue(lexer.nextToken(true).isIdent("fact"));
        assertTrue(lexer.nextToken(true).isKeyword("in"));
        assertTrue(lexer.nextToken(true).isIdent("fact"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol('='));
        assertTrue(lexer.nextToken(true).isKeyword("func"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol('('));
        assertTrue(lexer.nextToken(true).isIdent("x"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol(')'));
        assertTrue(lexer.nextToken(true).isKeyword("in"));
        assertTrue(lexer.nextToken(true).isKeyword("func"));
        assertTrue(lexer.nextToken(true).isIdent("fact_cps"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol('('));
        assertTrue(lexer.nextToken(true).isIdent("n"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol(','));
        assertTrue(lexer.nextToken(true).isIdent("k"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol(')'));
        assertTrue(lexer.nextToken(true).isKeyword("in"));
        assertTrue(lexer.nextToken(true).isKeyword("if"));
        assertTrue(lexer.nextToken(true).isIdent("n"));
        assertTrue(lexer.nextToken(true).isTwoCharSymbol("=="));
        assertTrue(lexer.nextToken(true).isInt("0"));
        assertTrue(lexer.nextToken(true).isKeyword("then"));
        assertTrue(lexer.nextToken(true).isIdent("k"));
        assertTrue(lexer.nextToken(true).isKeyword("else"));
        assertTrue(lexer.nextToken(true).isIdent("fact_cps"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol('('));
        assertTrue(lexer.nextToken(true).isIdent("n"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol('-'));
        assertTrue(lexer.nextToken(true).isInt("1"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol(','));
        assertTrue(lexer.nextToken(true).isIdent("n"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol('*'));
        assertTrue(lexer.nextToken(true).isIdent("k"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol(')'));
        assertTrue(lexer.nextToken(true).isKeyword("end"));
        assertTrue(lexer.nextToken(true).isKeyword("end"));
        assertTrue(lexer.nextToken(true).isIdent("fact_cps"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol('('));
        assertTrue(lexer.nextToken(true).isIdent("x"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol(','));
        assertTrue(lexer.nextToken(true).isInt("1"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol(')'));
        assertTrue(lexer.nextToken(true).isKeyword("end"));
        assertTrue(lexer.nextToken(true).isIdent("fact"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol('('));
        assertTrue(lexer.nextToken(true).isDec("100m"));
        assertTrue(lexer.nextToken(true).isOneCharSymbol(')'));
        assertTrue(lexer.nextToken(true).isKeyword("end"));
        assertTrue(lexer.nextToken(true).isEof());
    }

}
