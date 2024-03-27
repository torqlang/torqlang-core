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

public class TestParserProductExpr {

    @Test
    public void testIntTimesInt() {
        //                            012345
        Parser p = new Parser("3 * 5");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof ProductExpr);
        ProductExpr productExpr = (ProductExpr) sox;
        assertSourceSpan(productExpr, 0, 5);
        assertEquals("3 * 5", productExpr.toString());
        assertTrue(productExpr.arg1 instanceof IntAsExpr);
        assertEquals(Int32.I32_3, asIntAsExpr(productExpr.arg1).int64());
        assertSourceSpan(productExpr.arg1, 0, 1);
        assertEquals(ProductOper.MULTIPLY, productExpr.oper);
        assertEquals(Int32.I32_5, asIntAsExpr(productExpr.arg2).int64());
        assertSourceSpan(productExpr.arg2, 4, 5);
    }

    @Test
    public void testIntTimesNegativeInt() {
        //                            0123456
        Parser p = new Parser("3 * -5");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof ProductExpr);
        ProductExpr productExpr = (ProductExpr) sox;
        assertSourceSpan(productExpr, 0, 6);
        assertEquals("3 * -5", productExpr.toString());
        assertTrue(productExpr.arg1 instanceof IntAsExpr);
        assertEquals(Int32.I32_3, asIntAsExpr(productExpr.arg1).int64());
        assertSourceSpan(productExpr.arg1, 0, 1);
        assertEquals(ProductOper.MULTIPLY, productExpr.oper);
        UnaryExpr unaryExpr = asUnaryExpr(productExpr.arg2);
        assertEquals(UnaryOper.NEGATE, unaryExpr.oper);
        assertEquals(Int32.of(5), asIntAsExpr(unaryExpr.arg).int64());
        assertSourceSpan(productExpr.arg2, 4, 6);
    }

    @Test
    public void testSumOper() {
        assertEquals(ProductOper.DIVIDE, ProductOper.valueForSymbol("" + SymbolsAndKeywords.DIVIDE_OPER_CHAR));
        assertEquals(ProductOper.MODULO, ProductOper.valueForSymbol("" + SymbolsAndKeywords.MODULO_OPER_CHAR));
        assertEquals(ProductOper.MULTIPLY, ProductOper.valueForSymbol("" + SymbolsAndKeywords.MULTIPLY_OPER_CHAR));
    }

}
