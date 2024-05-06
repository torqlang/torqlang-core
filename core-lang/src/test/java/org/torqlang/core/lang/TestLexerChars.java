/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.torqlang.core.lang.CommonTools.getBoolean;

/**
 * CHAR_LITERAL: '&' (~'\\' | ESC_SEQ);
 * fragment ESC_SEQ: '\\' ([tbnrf'"`\\] | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT);
 */
public class TestLexerChars {

    @Test
    public void test01() {
        Lexer lexer;

        lexer = new Lexer("&a");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&a &b");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&b")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&a&b");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&b")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&a &b &c");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&b")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&c")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&a&b&c");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&b")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&c")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&a begin");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(lexer.nextToken().isKeyword("begin"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("begin &a");
        assertTrue(lexer.nextToken().isKeyword("begin"));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("begin &a end");
        assertTrue(lexer.nextToken().isKeyword("begin"));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(lexer.nextToken().isKeyword("end"));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&a . &b");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(lexer.nextToken().isOneCharSymbol('.'));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&b")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&0");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&0")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&0 &1");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&0")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&1")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&0 &1 &2");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&0")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&1")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&2")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0 &a");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isInt() && x.substring().equals("0")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&a 0");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isInt() && x.substring().equals("0")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("0 &a 1");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isInt() && x.substring().equals("0")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isInt() && x.substring().equals("1")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&a 0 &b");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isInt() && x.substring().equals("0")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&b")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("/* x */ &a");
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substring().equals("/* x */")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&a /* x */");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substring().equals("/* x */")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("/* x */ &a &b");
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substring().equals("/* x */")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&b")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&a /* x */ &b");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substring().equals("/* x */")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&b")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&a &b /* x */");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&b")));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substring().equals("/* x */")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("/*x*/&a&b");
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substring().equals("/*x*/")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&b")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&a/*x*/&b");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substring().equals("/*x*/")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&b")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&a&b/*x*/");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&a")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&b")));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substring().equals("/*x*/")));
        assertTrue(lexer.nextToken().isEof());
    }

    @Test
    public void test02() {
        Lexer lexer;

        lexer = new Lexer("&\\t");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\t")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&\\b");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\b")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&\\n");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\n")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&\\r");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\r")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&\\f");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\f")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&\\'");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\'")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&\\\"");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\\"")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&\\`");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\`")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&\\\\");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\\\")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&\\t&\\b");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\t")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\b")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&x&\\t&\\b");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&x")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\t")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\b")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&\\t&x&\\b");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\t")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&x")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\b")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&\\t&\\b&x");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\t")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\b")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&x")));
        assertTrue(lexer.nextToken().isEof());

        lexer = new Lexer("&x&\\t&y&\\b&z");
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&x")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\t")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&y")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&\\b")));
        assertTrue(getBoolean(lexer.nextToken(), (x) -> x.isChar() && x.substring().equals("&z")));
        assertTrue(lexer.nextToken().isEof());
    }

}
