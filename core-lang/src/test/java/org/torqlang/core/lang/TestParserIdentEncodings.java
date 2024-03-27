/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.torqlang.core.lang.CommonTools.asIdentAsExpr;

public class TestParserIdentEncodings {

    @Test
    public void test() {

        Parser p;
        SntcOrExpr sox;
        String v;

        p = new Parser("`\\u0078`");
        sox = p.parse();
        assertTrue(sox instanceof IdentAsExpr);
        v = asIdentAsExpr(sox).ident.name;
        assertEquals("x", v);

        p = new Parser("`\r`");
        sox = p.parse();
        assertTrue(sox instanceof IdentAsExpr);
        v = asIdentAsExpr(sox).ident.name;
        assertEquals("\r", v);

        p = new Parser("` \r\t `");
        sox = p.parse();
        assertTrue(sox instanceof IdentAsExpr);
        v = asIdentAsExpr(sox).ident.name;
        assertEquals(" \r\t ", v);

        p = new Parser("` `");
        sox = p.parse();
        assertTrue(sox instanceof IdentAsExpr);
        v = asIdentAsExpr(sox).ident.name;
        assertEquals(" ", v);
    }

}
