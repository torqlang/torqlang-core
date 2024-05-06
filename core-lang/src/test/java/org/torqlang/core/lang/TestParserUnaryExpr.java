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
import static org.torqlang.core.lang.CommonTools.*;
import static org.torqlang.core.lang.SymbolsAndKeywords.*;

public class TestParserUnaryExpr {

    @Test
    public void test() {
        //                            012
        Parser p = new Parser("@a");
        SntcOrExpr sox = p.parse();
        assertSourceSpan(sox, 0, 2);
        UnaryExpr unaryExpr = asUnaryExpr(sox);
        assertEquals(UnaryOper.ACCESS, unaryExpr.oper);
        assertEquals(UnaryOper.ACCESS, UnaryOper.valueForSymbol("" + ACCESS_CELL_VALUE_OPER_CHAR));
        IdentAsExpr identAsExpr = asIdentAsExpr(unaryExpr.arg);
        assertEquals(Ident.create("a"), identAsExpr.ident);
        assertEquals("@a", sox.toString());
        assertEquals("@a", LangFormatter.SINGLETON.format(sox));

        //                     012
        p = new Parser("-a");
        sox = p.parse();
        assertSourceSpan(sox, 0, 2);
        unaryExpr = asUnaryExpr(sox);
        assertEquals(UnaryOper.NEGATE, unaryExpr.oper);
        assertEquals(UnaryOper.NEGATE, UnaryOper.valueForSymbol("" + SUBTRACT_OPER_CHAR));
        identAsExpr = asIdentAsExpr(unaryExpr.arg);
        assertEquals(Ident.create("a"), identAsExpr.ident);
        assertEquals("-a", sox.toString());
        assertEquals("-a", LangFormatter.SINGLETON.format(sox));

        //                     012
        p = new Parser("!a");
        sox = p.parse();
        assertSourceSpan(sox, 0, 2);
        unaryExpr = asUnaryExpr(sox);
        assertEquals(UnaryOper.NOT, unaryExpr.oper);
        assertEquals(UnaryOper.NOT, UnaryOper.valueForSymbol("" + NOT_OPER_CHAR));
        identAsExpr = asIdentAsExpr(unaryExpr.arg);
        assertEquals(Ident.create("a"), identAsExpr.ident);
        assertEquals("!a", sox.toString());
        assertEquals("!a", LangFormatter.SINGLETON.format(sox));
    }

}
