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

public class TestParserOrExpr {

    @Test
    public void test() {
        //                            0123456
        Parser p = new Parser("a || b");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof OrExpr);
        OrExpr orExpr = (OrExpr) sox;
        assertSourceSpan(sox, 0, 6);
        assertEquals("a || b", orExpr.toString());
        assertTrue(orExpr.arg1 instanceof IdentAsExpr);
        assertEquals(Ident.create("a"), asIdentAsExpr(orExpr.arg1).ident);
        assertSourceSpan(orExpr.arg1, 0, 1);
        assertEquals(Ident.create("b"), asIdentAsExpr(orExpr.arg2).ident);
        assertSourceSpan(orExpr.arg2, 5, 6);
    }

}
