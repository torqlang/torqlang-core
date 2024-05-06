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

public class TestLexerComments {

    /**
     * Line comments
     */
    @Test
    public void test01() {
        String source;
        Lexer lexer;

        source = "//";
        lexer = new Lexer(source);
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("//")));
        assertTrue(lexer.nextToken().isEof());

        source = "// Line comment";
        lexer = new Lexer(source);
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("// Line comment")));
        assertTrue(lexer.nextToken().isEof());

        source = """
            // Line comment
            begin
                x
            end
            """;
        lexer = new Lexer(source);
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("// Line comment")));
        assertTrue(lexer.nextToken(false).isKeyword("begin"));
        assertTrue(lexer.nextToken(false).isIdent("x"));
        assertTrue(lexer.nextToken(false).isKeyword("end"));
        assertTrue(lexer.nextToken().isEof());

        source = """
            begin
                // Line comment
                x
            end
            """;
        lexer = new Lexer(source);
        assertTrue(lexer.nextToken(false).isKeyword("begin"));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("// Line comment")));
        assertTrue(lexer.nextToken(false).isIdent("x"));
        assertTrue(lexer.nextToken(false).isKeyword("end"));
        assertTrue(lexer.nextToken().isEof());

        source = """
            begin
                x
                // Line comment
            end
            """;
        lexer = new Lexer(source);
        assertTrue(lexer.nextToken(false).isKeyword("begin"));
        assertTrue(lexer.nextToken(false).isIdent("x"));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("// Line comment")));
        assertTrue(lexer.nextToken(false).isKeyword("end"));
        assertTrue(lexer.nextToken().isEof());

        source = """
            begin
                x
            end
            // Line comment
            """;
        lexer = new Lexer(source);
        assertTrue(lexer.nextToken(false).isKeyword("begin"));
        assertTrue(lexer.nextToken(false).isIdent("x"));
        assertTrue(lexer.nextToken(false).isKeyword("end"));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("// Line comment")));
        assertTrue(lexer.nextToken().isEof());

        source = """
            //
            // Line comment
            //
            """;
        lexer = new Lexer(source);
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("//")));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("// Line comment")));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("//")));
        assertTrue(lexer.nextToken().isEof());

        source = """
            begin
                x
            end
            //
            // Line comment
            //
            """;
        lexer = new Lexer(source);
        assertTrue(lexer.nextToken(false).isKeyword("begin"));
        assertTrue(lexer.nextToken(false).isIdent("x"));
        assertTrue(lexer.nextToken(false).isKeyword("end"));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("//")));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("// Line comment")));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("//")));
        assertTrue(lexer.nextToken().isEof());
    }

    /**
     * Block comments
     */
    @Test
    public void test02() {
        String source;
        Lexer lexer;

        source = "/**/";
        lexer = new Lexer(source);
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("/**/")));
        assertTrue(lexer.nextToken().isEof());

        source = "/*!*/";
        lexer = new Lexer(source);
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("/*!*/")));
        assertTrue(lexer.nextToken().isEof());

        source = """
            /*
            */
            """;
        lexer = new Lexer(source);
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("/*\n*/")));
        assertTrue(lexer.nextToken().isEof());

        source = """
            /*

            */
            """;
        lexer = new Lexer(source);
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("/*\n\n*/")));
        assertTrue(lexer.nextToken().isEof());

        source = """
            /*
                Block comment
            */
            """;
        lexer = new Lexer(source);
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("/*\n    Block comment\n*/")));
        assertTrue(lexer.nextToken().isEof());

        source = """
            /*
                Block comment 1
                Block comment 2
            */
            """;
        lexer = new Lexer(source);
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("/*\n    Block comment 1\n    Block comment 2\n*/")));
        assertTrue(lexer.nextToken().isEof());

        source = """
            /*
                Block comment 1
                // Hidden line comment
                Block comment 2
            */
            """;
        lexer = new Lexer(source);
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("/*\n    Block comment 1\n    // Hidden line comment\n    Block comment 2\n*/")));
        assertTrue(lexer.nextToken().isEof());

        source = """
            /*
                Block comment 1
                // Hidden line comment
                Block comment 2
            */
            begin
                x
            end
            """;
        lexer = new Lexer(source);
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("/*\n    Block comment 1\n    // Hidden line comment\n    Block comment 2\n*/")));
        assertTrue(lexer.nextToken(false).isKeyword("begin"));
        assertTrue(lexer.nextToken(false).isIdent("x"));
        assertTrue(lexer.nextToken(false).isKeyword("end"));
        assertTrue(lexer.nextToken().isEof());

        source = """
            begin
                /*
                    Block comment 1
                    // Hidden line comment
                    Block comment 2
                */
                x
            end
            """;
        lexer = new Lexer(source);
        assertTrue(lexer.nextToken(false).isKeyword("begin"));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("/*\n        Block comment 1\n        // Hidden line comment\n        Block comment 2\n    */")));
        assertTrue(lexer.nextToken(false).isIdent("x"));
        assertTrue(lexer.nextToken(false).isKeyword("end"));
        assertTrue(lexer.nextToken().isEof());

        source = """
            begin
                x
            end
            /*
                Block comment 1
                // Hidden line comment
                Block comment 2
            */
            """;
        lexer = new Lexer(source);
        assertTrue(lexer.nextToken(false).isKeyword("begin"));
        assertTrue(lexer.nextToken(false).isIdent("x"));
        assertTrue(lexer.nextToken(false).isKeyword("end"));
        assertTrue(getBoolean(lexer.nextToken(false), (x) -> x.isComment() && x.substringEquals("/*\n    Block comment 1\n    // Hidden line comment\n    Block comment 2\n*/")));
        assertTrue(lexer.nextToken().isEof());
    }

}
