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

public class TestParserSumExpr {

    @Test
    public void testIntPlusInt() {
        //                            012345
        Parser p = new Parser("3 + 5");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof SumExpr);
        SumExpr sumExpr = (SumExpr) sox;
        assertSourceSpan(sumExpr, 0, 5);
        assertEquals("3 + 5", sumExpr.toString());
        assertTrue(sumExpr.arg1 instanceof IntAsExpr);
        assertEquals(Int32.I32_3, asIntAsExpr(sumExpr.arg1).int64());
        assertSourceSpan(asIntAsExpr(sumExpr.arg1), 0, 1);
        assertEquals(SumOper.ADD, sumExpr.oper);
        assertEquals(Int32.I32_5, asIntAsExpr(sumExpr.arg2).int64());
        assertSourceSpan(asIntAsExpr(sumExpr.arg2), 4, 5);
    }

    @Test
    public void testIntPlusNegativeInt() {
        //                            0123456
        Parser p = new Parser("3 + -5");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof SumExpr);
        SumExpr sumExpr = (SumExpr) sox;
        assertSourceSpan(sumExpr, 0, 6);
        assertEquals("3 + -5", sumExpr.toString());
        assertTrue(sumExpr.arg1 instanceof IntAsExpr);
        assertEquals(Int32.I32_3, asIntAsExpr(sumExpr.arg1).int64());
        assertSourceSpan(asIntAsExpr(sumExpr.arg1), 0, 1);
        assertEquals(SumOper.ADD, sumExpr.oper);
        UnaryExpr unaryExpr = asUnaryExpr(sumExpr.arg2);
        assertEquals(UnaryOper.NEGATE, unaryExpr.oper);
        assertEquals(Int32.of(5), asIntAsExpr(unaryExpr.arg).int64());
        assertSourceSpan(sumExpr.arg2, 4, 6);
    }

    @Test
    public void testSumOper() {
        assertEquals(SumOper.ADD, SumOper.valueForSymbol("" + SymbolsAndKeywords.ADD_OPER_CHAR));
        assertEquals(SumOper.SUBTRACT, SumOper.valueForSymbol("" + SymbolsAndKeywords.SUBTRACT_OPER_CHAR));
    }

}
