/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.Int32;

import static org.junit.jupiter.api.Assertions.*;
import static org.torqlang.core.lang.CommonTools.asIntAsExpr;

public class TestParserRelationalExpr {

    @Test
    public void test() {
        //                            012345
        Parser p = new Parser("3 < 5");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(RelationalExpr.class, sox);
        RelationalExpr relExpr = (RelationalExpr) sox;
        CommonTools.assertSourceSpan(relExpr, 0, 5);
        assertEquals("3 < 5", relExpr.toString());
        assertInstanceOf(IntAsExpr.class, relExpr.arg1);
        assertEquals(Int32.I32_3, asIntAsExpr(relExpr.arg1).int64());
        CommonTools.assertSourceSpan(asIntAsExpr(relExpr.arg1), 0, 1);
        assertEquals(RelationalOper.LESS_THAN, relExpr.oper);
        assertEquals(Int32.I32_5, asIntAsExpr(relExpr.arg2).int64());
        CommonTools.assertSourceSpan(asIntAsExpr(relExpr.arg2), 4, 5);
    }

    @Test
    public void testRelationalOper() {
        assertEquals(RelationalOper.EQUAL_TO, RelationalOper.valueForSymbol(SymbolsAndKeywords.EQUAL_TO_OPER));
        assertEquals(RelationalOper.GREATER_THAN, RelationalOper.valueForSymbol("" + SymbolsAndKeywords.GREATER_THAN_OPER_CHAR));
        assertEquals(RelationalOper.GREATER_THAN_OR_EQUAL_TO, RelationalOper.valueForSymbol(SymbolsAndKeywords.GREATER_THAN_OR_EQUAL_TO_OPER));
        assertEquals(RelationalOper.LESS_THAN, RelationalOper.valueForSymbol("" + SymbolsAndKeywords.LESS_THAN_OPER_CHAR));
        assertEquals(RelationalOper.LESS_THAN_OR_EQUAL_TO, RelationalOper.valueForSymbol(SymbolsAndKeywords.LESS_THAN_OR_EQUAL_TO_OPER));
    }

}
