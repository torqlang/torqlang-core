/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.Ident;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.torqlang.core.lang.CommonTools.asIdentAsExpr;
import static org.torqlang.core.lang.CommonTools.assertSourceSpan;

public class TestParserUnifySntc {

    @Test
    public void test() {
        //                            012345
        Parser p = new Parser("a = b");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(UnifySntc.class, sox);
        UnifySntc unifySntc = (UnifySntc) sox;
        assertSourceSpan(unifySntc, 0, 5);
        // Test properties
        assertEquals(Ident.create("a"), asIdentAsExpr(unifySntc.leftSide).ident);
        assertSourceSpan(unifySntc.leftSide, 0, 1);
        assertEquals(Ident.create("b"), asIdentAsExpr(unifySntc.rightSide).ident);
        assertSourceSpan(unifySntc.rightSide, 4, 5);
        // Test toString format
        String expectedFormat = "a = b";
        String actualFormat = unifySntc.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "a = b";
        actualFormat = LangFormatter.SINGLETON.format(unifySntc);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testMultiline() {
        //                                      1
        //                            012345678901234567
        Parser p = new Parser("a = act b c d end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(UnifySntc.class, sox);
        UnifySntc unifySntc = (UnifySntc) sox;
        assertSourceSpan(unifySntc, 0, 17);
        // Test properties
        assertEquals(Ident.create("a"), asIdentAsExpr(unifySntc.leftSide).ident);
        assertSourceSpan(unifySntc.leftSide, 0, 1);
        assertInstanceOf(ActExpr.class, unifySntc.rightSide);
        assertSourceSpan(unifySntc.rightSide, 4, 17);
        // Test format
        String expectedFormat = """
            a = act
                b
                c
                d
            end""";
        String actualFormat = unifySntc.toString();
        assertEquals(expectedFormat, actualFormat);
    }

}
