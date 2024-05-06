/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Int32;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.torqlang.core.lang.CommonTools.assertSourceSpan;

public class TestParserBeginLang {

    @Test
    public void testBegin() {
        //                                      1
        //                            012345678901
        Parser p = new Parser("begin 1 end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(BeginLang.class, sox);
        BeginLang beginLang = (BeginLang) sox;
        assertSourceSpan(beginLang, 0, 11);
        assertSourceSpan(beginLang.body, 6, 7);
        assertEquals(1, beginLang.body.list.size());
        // Test format
        String expectedFormat = """
            begin
                1
            end""";
        String actualFormat = beginLang.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test part -- seq
        assertEquals(1, beginLang.body.list.size());
        SntcOrExpr sntcOrExpr = beginLang.body.list.get(0);
        assertSourceSpan(sntcOrExpr, 6, 7);
    }

    @Test
    public void testFormat() {
        //                                      1
        //                            0123456789012345
        Parser p = new Parser("begin 1 2 3 end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(BeginLang.class, sox);
        BeginLang beginLang = (BeginLang) sox;
        assertSourceSpan(beginLang, 0, 15);
        assertSourceSpan(beginLang.body, 6, 11);
        String expectedFormat = """
            begin
                1
                2
                3
            end""";
        String actualFormat = LangFormatter.SINGLETON.format(beginLang);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testSeq() {

        Ident a = Ident.create("a");
        Ident b = Ident.create("b");

        //                                      1
        //                            012345678901234567
        Parser p = new Parser("begin a 1 b 2 end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(BeginLang.class, sox);
        BeginLang beginLang = (BeginLang) sox;
        assertSourceSpan(beginLang, 0, 17);
        assertSourceSpan(beginLang.body, 6, 13);
        String expectedFormat = """
            begin
                a
                1
                b
                2
            end""";
        String actualFormat = LangFormatter.SINGLETON.format(beginLang);
        assertEquals(expectedFormat, actualFormat);
        assertEquals(4, beginLang.body.list.size());
        List<SntcOrExpr> list = beginLang.body.list;
        assertSourceSpan(list.get(0), 6, 7);
        assertEquals(a, CommonTools.asIdentAsExpr(list.get(0)).ident);
        assertSourceSpan(list.get(1), 8, 9);
        assertEquals(Int32.I32_1, CommonTools.asIntAsExpr(list.get(1)).int64());
        assertSourceSpan(list.get(2), 10, 11);
        assertEquals(b, CommonTools.asIdentAsExpr(list.get(2)).ident);
        assertSourceSpan(list.get(3), 12, 13);
        assertEquals(Int32.I32_2, CommonTools.asIntAsExpr(list.get(3)).int64());
    }

}
