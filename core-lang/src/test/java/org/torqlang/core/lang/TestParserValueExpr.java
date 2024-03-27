/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.*;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.torqlang.core.lang.CommonTools.*;

public class TestParserValueExpr {

    @Test
    public void test() {
        //                                      1         2         3         4         5
        //                            01234567890123456789012345678901234567890123456789012345678
        Parser p = new Parser("begin a 1 1L 1.0 1.0f 1m false true nothing eof &x 'x' end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof BeginLang);
        BeginLang beginLang = (BeginLang) sox;
        assertSourceSpan(beginLang, 0, 58);
        List<SntcOrExpr> list = beginLang.body.list;
        assertSourceSpan(list.get(0), 6, 7);
        assertEquals(Ident.create("a"), asIdentAsExpr(list.get(0)).ident);
        assertSourceSpan(list.get(1), 8, 9);
        assertEquals(Int32.I32_1, asIntAsExpr(list.get(1)).int64());
        assertSourceSpan(list.get(2), 10, 12);
        assertEquals(Int64.I64_1, asIntAsExpr(list.get(2)).int64());
        assertSourceSpan(list.get(3), 13, 16);
        assertEquals(Flt64.of(1.0), asFltAsExpr(list.get(3)).flt64());
        assertSourceSpan(list.get(4), 17, 21);
        assertEquals(Flt32.of(1.0f), asFltAsExpr(list.get(4)).flt64());
        assertSourceSpan(list.get(5), 22, 24);
        assertEquals(Dec128.of("1"), asDec128AsExpr(list.get(5)).dec128());
        assertSourceSpan(list.get(6), 25, 30);
        assertEquals(Bool.FALSE, asBoolAsExpr(list.get(6)).value());
        assertSourceSpan(list.get(7), 31, 35);
        assertEquals(Bool.TRUE, asBoolAsExpr(list.get(7)).value());
        assertSourceSpan(list.get(8), 36, 43);
        assertEquals(Nothing.SINGLETON, asNothingAsExpr(list.get(8)).value());
        assertSourceSpan(list.get(9), 44, 47);
        assertEquals(Eof.SINGLETON, asEofAsExpr(list.get(9)).value());
        assertSourceSpan(list.get(10), 48, 50);
        assertEquals(Char.of('x'), asCharAsExpr(list.get(10)).value());
        assertSourceSpan(list.get(11), 51, 54);
        assertEquals(Str.of("x"), asStrAsExpr(list.get(11)).value());
        String expectedFormat = """
            begin
                a
                1
                1L
                1.0
                1.0f
                1m
                false
                true
                nothing
                eof
                &x
                'x'
            end""";
        String actualFormat = beginLang.toString();
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testWithNegatives() {
        //                                      1         2         3         4
        //                            01234567890123456789012345678901234567890123
        Parser p = new Parser("begin a; -1; -1L; -1.0; -1.0f; -1m; -&x end");

        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof BeginLang);
        BeginLang beginLang = (BeginLang) sox;

        assertSourceSpan(beginLang, 0, 43);
        List<SntcOrExpr> list = beginLang.body.list;
        assertSourceSpan(list.get(0), 6, 7);
        assertEquals(Ident.create("a"), asIdentAsExpr(list.get(0)).ident);

        assertSourceSpan(list.get(1), 9, 11);
        UnaryExpr unaryExpr = asUnaryExpr(list.get(1));
        assertEquals(UnaryOper.NEGATE, unaryExpr.oper);
        assertEquals(Int32.of(1), asIntAsExpr(unaryExpr.arg).int64());

        assertSourceSpan(list.get(2), 13, 16);
        unaryExpr = asUnaryExpr(list.get(2));
        assertEquals(UnaryOper.NEGATE, unaryExpr.oper);
        assertEquals(Int64.of(1L), asIntAsExpr(unaryExpr.arg).int64());

        assertSourceSpan(list.get(3), 18, 22);
        unaryExpr = asUnaryExpr(list.get(3));
        assertEquals(UnaryOper.NEGATE, unaryExpr.oper);
        assertEquals(Flt64.of(1.0), asFltAsExpr(unaryExpr.arg).flt64());

        assertSourceSpan(list.get(4), 24, 29);
        unaryExpr = asUnaryExpr(list.get(4));
        assertEquals(UnaryOper.NEGATE, unaryExpr.oper);
        assertEquals(Flt32.of(1.0f), asFltAsExpr(unaryExpr.arg).flt64());

        assertSourceSpan(list.get(5), 31, 34);
        unaryExpr = asUnaryExpr(list.get(5));
        assertEquals(UnaryOper.NEGATE, unaryExpr.oper);
        assertEquals(Dec128.of("1"), asDec128AsExpr(unaryExpr.arg).dec128());

        String expectedFormat = """
            begin
                a
                -1
                -1L
                -1.0
                -1.0f
                -1m
                -&x
            end""";
        String actualFormat = beginLang.toString();
        assertEquals(expectedFormat, actualFormat);
    }

}
