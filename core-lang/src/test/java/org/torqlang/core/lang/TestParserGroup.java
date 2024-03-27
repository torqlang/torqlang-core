/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.Int32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.torqlang.core.lang.CommonTools.*;

public class TestParserGroup {

    @Test
    public void test() {
        //                            0123
        Parser p = new Parser("(1)");
        SntcOrExpr x = p.parse();
        assertSourceSpan(x, 0, 3);
        assertEquals(Int32.I32_1, asIntAsExpr(asSingleExpr(x)).int64());
        // Test toString format
        String expectedFormat = "(1)";
        String actualFormat = x.toString();
        assertEquals(expectedFormat, actualFormat);
        actualFormat = LangFormatter.SINGLETON.format(x);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testBegin() {
        //                                      1         2
        //                            0123456789012345678901234
        Parser p = new Parser("begin (1); (2); (-1) end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof BeginLang);
        BeginLang beginLang = (BeginLang) sox;

        assertSourceSpan(beginLang, 0, 24);

        assertTrue(beginLang.body.list.get(0) instanceof GroupExpr);
        assertEquals(Int32.I32_1, asIntAsExpr(asSingleExpr(beginLang.body.list.get(0))).int64());

        assertTrue(beginLang.body.list.get(1) instanceof GroupExpr);
        assertEquals(Int32.I32_2, asIntAsExpr(asSingleExpr(beginLang.body.list.get(1))).int64());

        assertTrue(beginLang.body.list.get(2) instanceof GroupExpr);
        UnaryExpr unaryExpr = asUnaryExpr(asSingleExpr(beginLang.body.list.get(2)));
        assertEquals(UnaryOper.NEGATE, unaryExpr.oper);
        assertEquals(Int32.of(1), asIntAsExpr(unaryExpr.arg).int64());

        // Test format
        String expectedFormat = """
            begin
                (1)
                (2)
                (-1)
            end""";
        String actualFormat = sox.toString();
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testMathGroups() {
        //                                      1         2
        //                            0123456789012345678901234567
        Parser p = new Parser("begin (1 + 5) * (3 - 7) end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof BeginLang);
        BeginLang beginLang = (BeginLang) sox;

        assertSourceSpan(beginLang, 0, 27);
        assertEquals(1, beginLang.body.list.size());
        assertTrue(asSingleExpr(beginLang.body) instanceof ProductExpr);

        ProductExpr productExpr = (ProductExpr) beginLang.body.list.get(0);
        assertSourceSpan(productExpr, 6, 23);

        assertTrue(productExpr.arg1 instanceof GroupExpr);
        assertSourceSpan(productExpr.arg1, 6, 13);
        assertSourceSpan(asSingleExpr(productExpr.arg1), 7, 12);
        assertTrue(asSingleExpr(productExpr.arg1) instanceof SumExpr);

        assertTrue(productExpr.arg2 instanceof GroupExpr);
        assertSourceSpan(productExpr.arg2, 16, 23);
        assertSourceSpan(asSingleExpr(productExpr.arg2), 17, 22);
        assertTrue(asSingleExpr(productExpr.arg2) instanceof SumExpr);
    }

}
