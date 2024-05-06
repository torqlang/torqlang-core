/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Int32;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.torqlang.core.lang.CommonTools.*;

public class TestParserProcLang {

    @Test
    public void testExpr() {
        //                                      1
        //                            01234567890123456
        Parser p = new Parser("proc () in 0 end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ProcExpr.class, sox);
        ProcExpr procExpr = (ProcExpr) sox;
        assertSourceSpan(procExpr, 0, 16);
        assertSourceSpan(procExpr.body, 11, 12);
        assertEquals(0, procExpr.formalArgs.size());
        assertEquals(1, procExpr.body.list.size());
        assertEquals(Int32.I32_0, asIntAsExpr(procExpr.body.list.get(0)).int64());
        // Test format
        String expectedFormat = """
            proc () in
                0
            end""";
        String actualFormat = procExpr.toString();
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testExprWithArgs1() {
        //                                      1
        //                            012345678901234567
        Parser p = new Parser("proc (a) in 0 end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ProcExpr.class, sox);
        ProcExpr procExpr = (ProcExpr) sox;
        assertSourceSpan(procExpr, 0, 17);
        assertSourceSpan(procExpr.body, 12, 13);
        assertEquals(1, procExpr.formalArgs.size());
        assertEquals(Ident.create("a"), asIdentAsPat(procExpr.formalArgs.get(0)).ident);
        assertEquals(1, procExpr.body.list.size());
        assertEquals(Int32.I32_0, asIntAsExpr(procExpr.body.list.get(0)).int64());
        // Test format
        String expectedFormat = """
            proc (a) in
                0
            end""";
        String actualFormat = procExpr.toString();
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testExprWithArgs2() {
        //                                      1         2
        //                            012345678901234567890
        Parser p = new Parser("proc (a, b) in 0 end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ProcExpr.class, sox);
        ProcExpr procExpr = (ProcExpr) sox;
        assertSourceSpan(procExpr, 0, 20);
        assertSourceSpan(procExpr.body, 15, 16);
        assertEquals(2, procExpr.formalArgs.size());
        assertEquals(Ident.create("a"), asIdentAsPat(procExpr.formalArgs.get(0)).ident);
        assertEquals(Ident.create("b"), asIdentAsPat(procExpr.formalArgs.get(1)).ident);
        assertEquals(1, procExpr.body.list.size());
        assertEquals(Int32.I32_0, asIntAsExpr(procExpr.body.list.get(0)).int64());
        // Test format
        String expectedFormat = """
            proc (a, b) in
                0
            end""";
        String actualFormat = procExpr.toString();
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testSntc() {
        //                                      1         2
        //                            01234567890123456789012
        Parser p = new Parser("proc MyProc() in 0 end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ProcSntc.class, sox);
        ProcSntc procSntc = (ProcSntc) sox;
        assertSourceSpan(procSntc, 0, 22);
        assertEquals(Ident.create("MyProc"), procSntc.name());
        assertSourceSpan(procSntc.body, 17, 18);
        assertEquals(0, procSntc.formalArgs.size());
        assertEquals(1, procSntc.body.list.size());
        assertEquals(Int32.I32_0, asIntAsExpr(procSntc.body.list.get(0)).int64());
        // Test format
        String expectedFormat = """
            proc MyProc() in
                0
            end""";
        String actualFormat = procSntc.toString();
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testSntcWithArgs1() {
        //                                      1         2
        //                            012345678901234567890123
        Parser p = new Parser("proc MyProc(a) in 0 end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ProcSntc.class, sox);
        ProcSntc procSntc = (ProcSntc) sox;
        assertSourceSpan(procSntc, 0, 23);
        assertEquals(Ident.create("MyProc"), procSntc.name());
        assertSourceSpan(procSntc.body, 18, 19);
        assertEquals(1, procSntc.formalArgs.size());
        assertEquals(Ident.create("a"), asIdentAsPat(procSntc.formalArgs.get(0)).ident);
        assertEquals(1, procSntc.body.list.size());
        assertEquals(Int32.I32_0, asIntAsExpr(procSntc.body.list.get(0)).int64());
        // Test format
        String expectedFormat = """
            proc MyProc(a) in
                0
            end""";
        String actualFormat = procSntc.toString();
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testSntcWithArgs2() {
        //                                      1         2
        //                            012345678901234567890123456
        Parser p = new Parser("proc MyProc(a, b) in 0 end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ProcSntc.class, sox);
        ProcSntc procSntc = (ProcSntc) sox;
        assertSourceSpan(procSntc, 0, 26);
        assertEquals(Ident.create("MyProc"), procSntc.name());
        assertSourceSpan(procSntc.body, 21, 22);
        assertEquals(2, procSntc.formalArgs.size());
        assertEquals(Ident.create("a"), asIdentAsPat(procSntc.formalArgs.get(0)).ident);
        assertEquals(Ident.create("b"), asIdentAsPat(procSntc.formalArgs.get(1)).ident);
        assertEquals(1, procSntc.body.list.size());
        assertEquals(Int32.I32_0, asIntAsExpr(procSntc.body.list.get(0)).int64());
        // Test format
        String expectedFormat = """
            proc MyProc(a, b) in
                0
            end""";
        String actualFormat = procSntc.toString();
        assertEquals(expectedFormat, actualFormat);
    }

}
