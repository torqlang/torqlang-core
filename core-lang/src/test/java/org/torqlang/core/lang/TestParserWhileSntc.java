/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.Bool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.torqlang.core.lang.CommonTools.*;

public class TestParserWhileSntc {

    @Test
    public void test() {
        //                                      1
        //                            01234567890123456789
        Parser p = new Parser("while true do a end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof WhileSntc);
        WhileSntc whileSntc = (WhileSntc) sox;
        assertSourceSpan(whileSntc, 0, 19);
        assertEquals(Bool.TRUE, asBoolAsExpr(whileSntc.cond).bool);
        assertSourceSpan(whileSntc.cond, 6, 10);
        // Test format
        String expectedFormat = """
            while true do
                a
            end""";
        String actualFormat = whileSntc.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test seq
        assertSourceSpan(whileSntc.body, 14, 15);
        assertEquals(1, whileSntc.body.list.size());
        IdentAsExpr identAsExpr = asIdentAsExpr(asSingleExpr(whileSntc.body));
        assertSourceSpan(identAsExpr, 14, 15);
    }

    @Test
    public void testSeqWithBreakContinueReturn() {
        //                                      1         2         3
        //                            0123456789012345678901234567890123456789
        Parser p = new Parser("while true do break continue return end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof WhileSntc);
        WhileSntc whileSntc = (WhileSntc) sox;
        assertSourceSpan(whileSntc, 0, 39);
        // Test format
        String expectedFormat = """
            while true do
                break
                continue
                return
            end""";
        String actualFormat = whileSntc.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test seq
        assertSourceSpan(whileSntc.body, 14, 35);
        assertEquals(3, whileSntc.body.list.size());
        assertTrue(whileSntc.body.list.get(0) instanceof BreakSntc);
        BreakSntc breakSntc = (BreakSntc) whileSntc.body.list.get(0);
        assertSourceSpan(breakSntc, 14, 19);
        assertTrue(whileSntc.body.list.get(1) instanceof ContinueSntc);
        ContinueSntc continueSntc = (ContinueSntc) whileSntc.body.list.get(1);
        assertSourceSpan(continueSntc, 20, 28);
        assertTrue(whileSntc.body.list.get(2) instanceof ReturnSntc);
        ReturnSntc returnSntc = (ReturnSntc) whileSntc.body.list.get(2);
        assertSourceSpan(returnSntc, 29, 35);
    }

}
