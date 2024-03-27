/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;
import org.torqlang.core.klvm.Ident;
import org.torqlang.core.klvm.Int32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.torqlang.core.lang.CommonTools.*;

public class TestParserFuncLang {

    @Test
    public void testExpr() {
        //                                      1
        //                            01234567890123456
        Parser p = new Parser("func () in 0 end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof FuncExpr);
        FuncExpr funcExpr = (FuncExpr) sox;
        assertSourceSpan(funcExpr, 0, 16);
        assertSourceSpan(funcExpr.body, 11, 12);
        assertEquals(0, funcExpr.formalArgs.size());
        assertEquals(1, funcExpr.body.list.size());
        assertEquals(Int32.I32_0, asIntAsExpr(funcExpr.body.list.get(0)).int64());
        // Test format
        String expectedFormat = """
            func () in
                0
            end""";
        String actualFormat = funcExpr.toString();
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testExprWithArgs1() {
        //                                      1
        //                            012345678901234567
        Parser p = new Parser("func (a) in 0 end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof FuncExpr);
        FuncExpr funcExpr = (FuncExpr) sox;
        assertSourceSpan(funcExpr, 0, 17);
        assertSourceSpan(funcExpr.body, 12, 13);
        assertEquals(1, funcExpr.formalArgs.size());
        assertEquals(Ident.create("a"), asIdentAsPat(funcExpr.formalArgs.get(0)).ident);
        assertEquals(1, funcExpr.body.list.size());
        assertEquals(Int32.I32_0, asIntAsExpr(funcExpr.body.list.get(0)).int64());
        // Test format
        String expectedFormat = """
            func (a) in
                0
            end""";
        String actualFormat = funcExpr.toString();
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testExprWithArgs2() {
        //                                      1         2
        //                            012345678901234567890
        Parser p = new Parser("func (a, b) in 0 end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof FuncExpr);
        FuncExpr funcExpr = (FuncExpr) sox;
        assertSourceSpan(funcExpr, 0, 20);
        assertSourceSpan(funcExpr.body, 15, 16);
        assertEquals(2, funcExpr.formalArgs.size());
        assertEquals(Ident.create("a"), asIdentAsPat(funcExpr.formalArgs.get(0)).ident);
        assertEquals(Ident.create("b"), asIdentAsPat(funcExpr.formalArgs.get(1)).ident);
        assertEquals(1, funcExpr.body.list.size());
        assertEquals(Int32.I32_0, asIntAsExpr(funcExpr.body.list.get(0)).int64());
        // Test format
        String expectedFormat = """
            func (a, b) in
                0
            end""";
        String actualFormat = funcExpr.toString();
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testSntc() {
        //                                      1         2
        //                            01234567890123456789012
        Parser p = new Parser("func MyFunc() in 0 end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof FuncSntc);
        FuncSntc funcSntc = (FuncSntc) sox;
        assertSourceSpan(funcSntc, 0, 22);
        assertEquals(Ident.create("MyFunc"), funcSntc.name());
        assertSourceSpan(funcSntc.body, 17, 18);
        assertEquals(0, funcSntc.formalArgs.size());
        assertEquals(1, funcSntc.body.list.size());
        assertEquals(Int32.I32_0, asIntAsExpr(funcSntc.body.list.get(0)).int64());
        // Test format
        String expectedFormat = """
            func MyFunc() in
                0
            end""";
        String actualFormat = funcSntc.toString();
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testSntcWithArgs1() {
        //                                      1         2
        //                            012345678901234567890123
        Parser p = new Parser("func MyFunc(a) in 0 end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof FuncSntc);
        FuncSntc funcSntc = (FuncSntc) sox;
        assertSourceSpan(funcSntc, 0, 23);
        assertEquals(Ident.create("MyFunc"), funcSntc.name());
        assertSourceSpan(funcSntc.body, 18, 19);
        assertEquals(1, funcSntc.formalArgs.size());
        assertEquals(Ident.create("a"), asIdentAsPat(funcSntc.formalArgs.get(0)).ident);
        assertEquals(1, funcSntc.body.list.size());
        assertEquals(Int32.I32_0, asIntAsExpr(funcSntc.body.list.get(0)).int64());
        // Test format
        String expectedFormat = """
            func MyFunc(a) in
                0
            end""";
        String actualFormat = funcSntc.toString();
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testSntcWithArgs2() {
        //                                      1         2
        //                            012345678901234567890123456
        Parser p = new Parser("func MyFunc(a, b) in 0 end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof FuncSntc);
        FuncSntc funcSntc = (FuncSntc) sox;
        assertSourceSpan(funcSntc, 0, 26);
        assertEquals(Ident.create("MyFunc"), funcSntc.name());
        assertSourceSpan(funcSntc.body, 21, 22);
        assertEquals(2, funcSntc.formalArgs.size());
        assertEquals(Ident.create("a"), asIdentAsPat(funcSntc.formalArgs.get(0)).ident);
        assertEquals(Ident.create("b"), asIdentAsPat(funcSntc.formalArgs.get(1)).ident);
        assertEquals(1, funcSntc.body.list.size());
        assertEquals(Int32.I32_0, asIntAsExpr(funcSntc.body.list.get(0)).int64());
        // Test format
        String expectedFormat = """
            func MyFunc(a, b) in
                0
            end""";
        String actualFormat = funcSntc.toString();
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testSntcWithArgs2WithReturnAnno() {
        //                                      1         2         3
        //                            012345678901234567890123456789012345
        Parser p = new Parser("func MyFunc(a, b) -> Int32 in 0 end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof FuncSntc);
        FuncSntc funcSntc = (FuncSntc) sox;
        assertSourceSpan(funcSntc, 0, 35);
        assertEquals(Ident.create("MyFunc"), funcSntc.name());
        assertSourceSpan(funcSntc.body, 30, 31);
        assertEquals(2, funcSntc.formalArgs.size());
        assertEquals(Ident.create("a"), asIdentAsPat(funcSntc.formalArgs.get(0)).ident);
        assertEquals(Ident.create("b"), asIdentAsPat(funcSntc.formalArgs.get(1)).ident);
        assertEquals(1, funcSntc.body.list.size());
        assertEquals(Int32.I32_0, asIntAsExpr(funcSntc.body.list.get(0)).int64());
        // Test format
        String expectedFormat = """
            func MyFunc(a, b) -> Int32 in
                0
            end""";
        String actualFormat = funcSntc.toString();
        assertEquals(expectedFormat, actualFormat);
    }

}
