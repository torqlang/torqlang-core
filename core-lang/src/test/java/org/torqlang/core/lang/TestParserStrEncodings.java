/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.torqlang.core.lang.CommonTools.asStrAsExpr;

public class TestParserStrEncodings {

    @Test
    public void testEscapeChars() {

        Parser p;
        SntcOrExpr sox;
        String v;

        p = new Parser("'\\t\\b\\n\\r\\f'");
        sox = p.parse();
        assertInstanceOf(StrAsExpr.class, sox);
        v = asStrAsExpr(sox).str.value;
        assertEquals("<<0009>><<0008>><<000a>><<000d>><<000c>>", toStringWithEscapedControlCodes(v));

        {
            p = new Parser("'\\x'");
            Exception exc = assertThrows(IllegalArgumentException.class, p::parse);
            assertEquals("Invalid escape sequence: \\x", exc.getMessage());
        }
    }

    @Test
    public void testNewLines() {
        //                         1         2         3         4         5         6         7         8
        //               012345678901234567890123456789012345678901234567890123456789012345678901234567890
        String source = "'We hold these truths to be self-evident,\\nthat all men are created equal, ...'";
        assertEquals(79, source.length()); // Prove that double backslash becomes just one
        String expected = """
            We hold these truths to be self-evident,
            that all men are created equal, ...""";
        assertEquals(76, expected.length()); // Prove that expected source is 3 less than given source
        Parser p = new Parser(source);
        SntcOrExpr sox = p.parse();
        assertInstanceOf(StrAsExpr.class, sox);
        String v = asStrAsExpr(sox).str.value;
        // The resulting string length should be 3 less because of two missing single quotes and one escape
        assertEquals(source.length() - 3, v.length());
        assertEquals(expected, v);
        assertNotEquals(toStringWithEscapedControlCodes(source), toStringWithEscapedControlCodes(v));
        assertEquals("We hold these truths to be self-evident,<<000a>>that all men are created equal, ...",
            toStringWithEscapedControlCodes(v));
    }

    @Test
    public void testUnicode() {

        Parser p;
        SntcOrExpr sox;
        String v;

        p = new Parser("'\\u0078'");
        sox = p.parse();
        assertInstanceOf(StrAsExpr.class, sox);
        v = asStrAsExpr(sox).str.value;
        assertEquals("x", v);

        p = new Parser("'\\u0078\\u0079'");
        sox = p.parse();
        assertInstanceOf(StrAsExpr.class, sox);
        v = asStrAsExpr(sox).str.value;
        assertEquals("xy", v);

        p = new Parser("'\\u0078\\u0079\\u007A'");
        sox = p.parse();
        assertInstanceOf(StrAsExpr.class, sox);
        v = asStrAsExpr(sox).str.value;
        assertEquals("xyz", v);
    }

    private String toStringWithEscapedControlCodes(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 2);
        CommonTools.toStringWithEscapedControlCodes(s, sb);
        return sb.toString();
    }

}
