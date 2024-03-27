/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestLexerSymbols {

    /**
     * Empty source
     */
    @Test
    public void test01() {
        // End of file
        Lexer lexer = new Lexer("");
        LexerToken t = lexer.nextToken();
        assertTrue(t.isEof());
    }

    /**
     * Single token
     */
    @Test
    public void test02() {
        Lexer lexer = new Lexer(";");
        LexerToken t = lexer.nextToken();
        assertTrue(t.isOneCharSymbol(';'));
    }

    /**
     * Parse keywords
     */
    @Test
    public void test03() {
        String keywords = """
            act actor ask begin break case catch continue do else elseif end eof false finally for func if
            import in local nothing of proc return self skip spawn tell then throw true try var when while""";
        Lexer lexer = new Lexer(keywords);
        assertTrue(lexer.nextToken().isKeyword("act"));
        assertTrue(lexer.nextToken().isKeyword("actor"));
        assertTrue(lexer.nextToken().isContextualKeyword("ask"));
        assertTrue(lexer.nextToken().isKeyword("begin"));
        assertTrue(lexer.nextToken().isKeyword("break"));
        assertTrue(lexer.nextToken().isKeyword("case"));
        assertTrue(lexer.nextToken().isKeyword("catch"));
        assertTrue(lexer.nextToken().isKeyword("continue"));
        assertTrue(lexer.nextToken().isKeyword("do"));
        assertTrue(lexer.nextToken().isKeyword("else"));
        assertTrue(lexer.nextToken().isKeyword("elseif"));
        assertTrue(lexer.nextToken().isKeyword("end"));
        assertTrue(lexer.nextToken().isKeyword("eof"));
        assertTrue(lexer.nextToken().isKeyword("false"));
        assertTrue(lexer.nextToken().isKeyword("finally"));
        assertTrue(lexer.nextToken().isKeyword("for"));
        assertTrue(lexer.nextToken().isKeyword("func"));
        assertTrue(lexer.nextToken().isKeyword("if"));
        assertTrue(lexer.nextToken().isKeyword("import"));
        assertTrue(lexer.nextToken().isKeyword("in"));
        assertTrue(lexer.nextToken().isKeyword("local"));
        assertTrue(lexer.nextToken().isKeyword("nothing"));
        assertTrue(lexer.nextToken().isKeyword("of"));
        assertTrue(lexer.nextToken().isKeyword("proc"));
        assertTrue(lexer.nextToken().isKeyword("return"));
        assertTrue(lexer.nextToken().isKeyword("self"));
        assertTrue(lexer.nextToken().isKeyword("skip"));
        assertTrue(lexer.nextToken().isKeyword("spawn"));
        assertTrue(lexer.nextToken().isContextualKeyword("tell"));
        assertTrue(lexer.nextToken().isKeyword("then"));
        assertTrue(lexer.nextToken().isKeyword("throw"));
        assertTrue(lexer.nextToken().isKeyword("true"));
        assertTrue(lexer.nextToken().isKeyword("try"));
        assertTrue(lexer.nextToken().isKeyword("var"));
        assertTrue(lexer.nextToken().isKeyword("when"));
        assertTrue(lexer.nextToken().isKeyword("while"));
        assertTrue(lexer.nextToken().isEof());
    }

    /**
     * Parse operators
     * <pre>
     * | Operator | Description of Operator | Priority | Associativity |
     * |----------|-------------------------|----------|---------------|
     * | @        | Get cell value          | 1        | Left to right |
     * | .        | Select                  | 2        | Left to right |
     * | []       | Select                  | 2        | Left to right |
     * | ()       | Apply                   | 2        | Left to right |
     * | !        | Logical NOT             | 3        | Right to left |
     * | –        | Negate                  | 3        | Right to left |
     * | %        | Remainder               | 4        | Left to right |
     * | /        | Divide                  | 4        | Left to right |
     * | *        | Multiply                | 4        | Left to right |
     * | –        | Subtraction             | 5        | Left to right |
     * | +        | Addition                | 5        | Left to right |
     * | >        | Greater than            | 6        | Left to right |
     * | <        | Less than               | 6        | Left to right |
     * | >=       | Greater than or equal   | 6        | Left to right |
     * | <=       | Less than or equal      | 6        | Left to right |
     * | ==       | Equal to                | 6        | Left to right |
     * | !=       | Not equal to            | 6        | Left to right |
     * | &&       | Logical AND             | 7        | Left to right |
     * | \|\|     | Logical OR              | 8        | Left to right |
     * | =        | Assign value            | 9        | Right to left |
     * | =:       | Set cell value          | 9        | Right to left |
     * </pre>
     */
    @Test
    public void test04() {
        String symbols = "@ . { } [ ] ( ) ! - % / * + > < >= <= == != && || = :=";
        Lexer lexer = new Lexer(symbols);
        assertTrue(lexer.nextToken().isOneCharSymbol('@'));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isOneCharSymbol('{'));
        assertTrue(lexer.nextToken().isOneCharSymbol('}'));
        assertTrue(lexer.nextToken().isOneCharSymbol('['));
        assertTrue(lexer.nextToken().isOneCharSymbol(']'));
        assertTrue(lexer.nextToken().isOneCharSymbol('('));
        assertTrue(lexer.nextToken().isOneCharSymbol(')'));
        assertTrue(lexer.nextToken().isOneCharSymbol('!'));
        assertTrue(lexer.nextToken().isOneCharSymbol('-'));
        assertTrue(lexer.nextToken().isOneCharSymbol('%'));
        assertTrue(lexer.nextToken().isOneCharSymbol('/'));
        assertTrue(lexer.nextToken().isOneCharSymbol('*'));
        assertTrue(lexer.nextToken().isOneCharSymbol('+'));
        assertTrue(lexer.nextToken().isOneCharSymbol('>'));
        assertTrue(lexer.nextToken().isOneCharSymbol('<'));
        assertTrue(lexer.nextToken().isTwoCharSymbol(">="));
        assertTrue(lexer.nextToken().isTwoCharSymbol("<="));
        assertTrue(lexer.nextToken().isTwoCharSymbol("=="));
        assertTrue(lexer.nextToken().isTwoCharSymbol("!="));
        assertTrue(lexer.nextToken().isTwoCharSymbol("&&"));
        assertTrue(lexer.nextToken().isTwoCharSymbol("||"));
        assertTrue(lexer.nextToken().isOneCharSymbol('='));
        assertTrue(lexer.nextToken().isTwoCharSymbol(":="));
        assertTrue(lexer.nextToken().isEof());
    }

    /**
     * Other symbols
     */
    @Test
    public void test05() {
        String symbols = "... : -> # :: <: >: , ; ~";
        Lexer lexer = new Lexer(symbols);
        assertTrue(lexer.nextToken().isThreeCharSymbol("..."));
        assertTrue(lexer.nextToken().isOneCharSymbol(':'));
        assertTrue(lexer.nextToken().isTwoCharSymbol("->"));
        assertTrue(lexer.nextToken().isOneCharSymbol('#'));
        assertTrue(lexer.nextToken().isTwoCharSymbol("::"));
        assertTrue(lexer.nextToken().isTwoCharSymbol("<:"));
        assertTrue(lexer.nextToken().isTwoCharSymbol(">:"));
        assertTrue(lexer.nextToken().isOneCharSymbol(','));
        assertTrue(lexer.nextToken().isOneCharSymbol(';'));
        assertTrue(lexer.nextToken().isOneCharSymbol('~'));
        assertTrue(lexer.nextToken().isEof());
    }

    /**
     * No whitespace
     */
    @Test
    public void test06() {
        // We used the above sequences but had to swap '/' and '*' to
        // prevent parsing an "open comment"
        String symbols = "@.{}[]()!-%*/+><>=<===!=&&||=:=...:->#::<:>:,;~";
        Lexer lexer = new Lexer(symbols);
        assertTrue(lexer.nextToken().isOneCharSymbol('@'));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(lexer.nextToken().isOneCharSymbol('{'));
        assertTrue(lexer.nextToken().isOneCharSymbol('}'));
        assertTrue(lexer.nextToken().isOneCharSymbol('['));
        assertTrue(lexer.nextToken().isOneCharSymbol(']'));
        assertTrue(lexer.nextToken().isOneCharSymbol('('));
        assertTrue(lexer.nextToken().isOneCharSymbol(')'));
        assertTrue(lexer.nextToken().isOneCharSymbol('!'));
        assertTrue(lexer.nextToken().isOneCharSymbol('-'));
        assertTrue(lexer.nextToken().isOneCharSymbol('%'));
        assertTrue(lexer.nextToken().isOneCharSymbol('*'));
        assertTrue(lexer.nextToken().isOneCharSymbol('/'));
        assertTrue(lexer.nextToken().isOneCharSymbol('+'));
        assertTrue(lexer.nextToken().isOneCharSymbol('>'));
        assertTrue(lexer.nextToken().isOneCharSymbol('<'));
        assertTrue(lexer.nextToken().isTwoCharSymbol(">="));
        assertTrue(lexer.nextToken().isTwoCharSymbol("<="));
        assertTrue(lexer.nextToken().isTwoCharSymbol("=="));
        assertTrue(lexer.nextToken().isTwoCharSymbol("!="));
        assertTrue(lexer.nextToken().isTwoCharSymbol("&&"));
        assertTrue(lexer.nextToken().isTwoCharSymbol("||"));
        assertTrue(lexer.nextToken().isOneCharSymbol('='));
        assertTrue(lexer.nextToken().isTwoCharSymbol(":="));
        assertTrue(lexer.nextToken().isThreeCharSymbol("..."));
        assertTrue(lexer.nextToken().isOneCharSymbol(':'));
        assertTrue(lexer.nextToken().isTwoCharSymbol("->"));
        assertTrue(lexer.nextToken().isOneCharSymbol('#'));
        assertTrue(lexer.nextToken().isTwoCharSymbol("::"));
        assertTrue(lexer.nextToken().isTwoCharSymbol("<:"));
        assertTrue(lexer.nextToken().isTwoCharSymbol(">:"));
        assertTrue(lexer.nextToken().isOneCharSymbol(','));
        assertTrue(lexer.nextToken().isOneCharSymbol(';'));
        assertTrue(lexer.nextToken().isOneCharSymbol('~'));
        assertTrue(lexer.nextToken().isEof());
    }

}
