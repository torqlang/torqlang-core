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
import static org.torqlang.core.lang.CommonTools.*;

public class TestParserApplyExpr {

    @Test
    public void test() {
        //                            0123
        Parser p = new Parser("x()");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof ApplyLang);
        ApplyLang applyLang = (ApplyLang) sox;
        assertSourceSpan(applyLang, 0, 3);
        assertEquals(Ident.create("x"), asIdentAsExpr(applyLang.proc).ident);
        assertSourceSpan(applyLang.proc, 0, 1);
        assertEquals(0, applyLang.args.size());
        // Test toString format
        String expectedFormat = "x()";
        String actualFormat = applyLang.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "x()";
        actualFormat = LangFormatter.SINGLETON.format(applyLang);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testArgs1() {
        //                            01234
        Parser p = new Parser("x(a)");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof ApplyLang);
        ApplyLang applyLang = (ApplyLang) sox;
        assertSourceSpan(applyLang, 0, 4);
        assertEquals(Ident.create("x"), asIdentAsExpr(applyLang.proc).ident);
        assertSourceSpan(applyLang.proc, 0, 1);
        assertEquals(1, applyLang.args.size());
        assertTrue(applyLang.args.get(0) instanceof IdentAsExpr);
        assertSourceSpan(applyLang.args.get(0), 2, 3);
        // Test toString format
        String expectedFormat = "x(a)";
        String actualFormat = applyLang.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "x(a)";
        actualFormat = LangFormatter.SINGLETON.format(applyLang);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testArgs2() {
        //                            01234567
        Parser p = new Parser("x(a, 3)");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof ApplyLang);
        ApplyLang applyLang = (ApplyLang) sox;
        assertSourceSpan(applyLang, 0, 7);
        assertEquals(Ident.create("x"), asIdentAsExpr(applyLang.proc).ident);
        assertSourceSpan(applyLang.proc, 0, 1);
        assertEquals(2, applyLang.args.size());
        assertTrue(applyLang.args.get(0) instanceof IdentAsExpr);
        assertSourceSpan(applyLang.args.get(0), 2, 3);
        assertTrue(applyLang.args.get(1) instanceof IntAsExpr);
        assertSourceSpan(applyLang.args.get(1), 5, 6);
        // Test toString format
        String expectedFormat = "x(a, 3)";
        String actualFormat = applyLang.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "x(a, 3)";
        actualFormat = LangFormatter.SINGLETON.format(applyLang);
        assertEquals(expectedFormat, actualFormat);
    }

}
