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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.torqlang.core.lang.CommonTools.*;

public class TestParserForSntc {

    @Test
    public void test() {
        //                                      1         2
        //                            012345678901234567890123456
        Parser p = new Parser("for n in range do skip end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(ForSntc.class, sox);
        ForSntc forSntc = (ForSntc) sox;
        assertSourceSpan(forSntc, 0, 26);
        assertEquals(Ident.create("n"), asIdentAsPat(forSntc.pat).ident);
        assertSourceSpan(forSntc.pat, 4, 5);
        assertEquals(Ident.create("range"), asIdentAsExpr(forSntc.iter).ident);
        assertSourceSpan(forSntc.iter, 9, 14);
        // Test body
        assertEquals(1, forSntc.body.list.size());
        assertSourceSpan(forSntc.body, 18, 22);
        assertInstanceOf(SkipSntc.class, forSntc.body.list.get(0));
        // Test format
        String expectedFormat = """
            for n in range do
                skip
            end""";
        String actualFormat = forSntc.toString();
        assertEquals(expectedFormat, actualFormat);
    }

}
