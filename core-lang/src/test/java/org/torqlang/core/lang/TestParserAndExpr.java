/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.Ident;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.torqlang.core.lang.CommonTools.asIdentAsExpr;
import static org.torqlang.core.lang.CommonTools.assertSourceSpan;

public class TestParserAndExpr {

    @Test
    public void test() {
        //                            0123456
        Parser p = new Parser("a && b");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof AndExpr);
        AndExpr andExpr = (AndExpr) sox;
        assertSourceSpan(sox, 0, 6);
        assertEquals("a && b", andExpr.toString());
        assertTrue(andExpr.arg1 instanceof IdentAsExpr);
        assertEquals(Ident.create("a"), asIdentAsExpr(andExpr.arg1).ident);
        assertSourceSpan(andExpr.arg1, 0, 1);
        assertEquals(Ident.create("b"), asIdentAsExpr(andExpr.arg2).ident);
        assertSourceSpan(andExpr.arg2, 5, 6);
    }

}
