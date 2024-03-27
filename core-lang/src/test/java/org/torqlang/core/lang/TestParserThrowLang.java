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

public class TestParserThrowLang {

    @Test
    public void test() {
        //                            01234567
        Parser p = new Parser("throw x");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof ThrowLang);
        ThrowLang throwLang = (ThrowLang) sox;
        assertSourceSpan(throwLang, 0, 7);
        assertEquals(Ident.create("x"), asIdentAsExpr(throwLang.arg).ident);
        assertSourceSpan(throwLang.arg, 6, 7);
        // Test toString format
        String expectedFormat = "throw x";
        String actualFormat = throwLang.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test indented format
        expectedFormat = "throw x";
        actualFormat = LangFormatter.SINGLETON.format(throwLang);
        assertEquals(expectedFormat, actualFormat);
    }

}
