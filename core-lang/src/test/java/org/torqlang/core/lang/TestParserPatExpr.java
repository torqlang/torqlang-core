/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;
import org.torqlang.core.klvm.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.torqlang.core.lang.CommonTools.*;

public class TestParserPatExpr {

    @Test
    public void test() {
        //                                      1         2         3         4         5         6         7
        //                            012345678901234567890123456789012345678901234567890123456789012345678901234
        Parser p = new Parser("case z of [a, 1, 1L, false, true, null, eof, 'x'] then true else false end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(CaseLang.class, sox);
        CaseLang caseLang = (CaseLang) sox;
        assertSourceSpan(caseLang, 0, 74);
        assertSourceSpan(caseLang.caseClause, 7, 59);
        // Test format
        String expectedFormat = """
            case z
                of [a, 1, 1L, false, true, null, eof, 'x'] then
                    true
                else
                    false
            end""";
        String actualFormat = caseLang.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test tuple properties
        assertInstanceOf(TuplePat.class, caseLang.caseClause.pat);
        TuplePat tp = (TuplePat) caseLang.caseClause.pat;
        assertNull(tp.label());
        assertFalse(tp.partialArity());
        // Test expected tuple values
        assertEquals(8, tp.values().size());
        assertEquals(Ident.create("a"), asIdentAsPat(tp.values().get(0)).ident);
        assertEquals(Int32.I32_1, asIntAsPat(tp.values().get(1)).value());
        assertEquals(Int64.I64_1, asIntAsPat(tp.values().get(2)).value());
        assertEquals(Bool.FALSE, asBoolAsPat(tp.values().get(3)).value());
        assertEquals(Bool.TRUE, asBoolAsPat(tp.values().get(4)).value());
        assertEquals(Null.SINGLETON, asNullAsPat(tp.values().get(5)).value());
        assertEquals(Eof.SINGLETON, asEofAsPat(tp.values().get(6)).value());
        assertEquals(Str.of("x"), asStrAsPat(tp.values().get(7)).value());
    }

    @Test
    public void testWithLabel() {
        //                                      1         2         3         4         5         6         7         8
        //                            01234567890123456789012345678901234567890123456789012345678901234567890123456789012345
        Parser p = new Parser("case z of 'my-label'#[a, 1, 1L, false, true, null, eof, 'x'] then true else false end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(CaseLang.class, sox);
        CaseLang caseLang = (CaseLang) sox;
        assertSourceSpan(caseLang, 0, 85);
        assertSourceSpan(caseLang.caseClause, 7, 70);
        // Test format
        String expectedFormat = """
            case z
                of 'my-label'#[a, 1, 1L, false, true, null, eof, 'x'] then
                    true
                else
                    false
            end""";
        String actualFormat = caseLang.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test tuple properties
        assertInstanceOf(TuplePat.class, caseLang.caseClause.pat);
        TuplePat tp = (TuplePat) caseLang.caseClause.pat;
        assertEquals(Str.of("my-label"), asStrAsPat(tp.label()).value());
        assertFalse(tp.partialArity());
        // Test expected tuple values
        assertEquals(8, tp.values().size());
        assertEquals(Ident.create("a"), asIdentAsPat(tp.values().get(0)).ident);
        assertEquals(Int32.I32_1, asIntAsPat(tp.values().get(1)).value());
        assertEquals(Int64.I64_1, asIntAsPat(tp.values().get(2)).value());
        assertEquals(Bool.FALSE, asBoolAsPat(tp.values().get(3)).value());
        assertEquals(Bool.TRUE, asBoolAsPat(tp.values().get(4)).value());
        assertEquals(Null.SINGLETON, asNullAsPat(tp.values().get(5)).value());
        assertEquals(Eof.SINGLETON, asEofAsPat(tp.values().get(6)).value());
        assertEquals(Str.of("x"), asStrAsPat(tp.values().get(7)).value());
    }

}
