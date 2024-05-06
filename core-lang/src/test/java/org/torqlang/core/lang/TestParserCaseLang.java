/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.torqlang.core.lang.CommonTools.assertSourceSpan;

public class TestParserCaseLang {

    @Test
    public void testCase() {
        //                                      1         2
        //                            01234567890123456789012345
        Parser p = new Parser("case 0 of 0 then true end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(CaseLang.class, sox);
        CaseLang caseLang = (CaseLang) sox;
        assertSourceSpan(caseLang, 0, 25);
        assertSourceSpan(caseLang.arg, 5, 6);
        // Test format
        String expectedFormat = """
            case 0
                of 0 then
                    true
            end""";
        String actualFormat = caseLang.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test part -- caseClause
        assertSourceSpan(caseLang.caseClause, 7, 21);
        assertInstanceOf(IntAsPat.class, caseLang.caseClause.pat);
        assertSourceSpan(caseLang.caseClause.pat, 10, 11);
        // Test part -- caseClause body
        assertEquals(1, caseLang.caseClause.body.list.size());
        SntcOrExpr bodyExpr = caseLang.caseClause.body.list.get(0);
        assertInstanceOf(BoolAsExpr.class, bodyExpr);
        assertSourceSpan(bodyExpr, 17, 21);
        // Test part -- altCaseClauses
        assertEquals(0, caseLang.altCaseClauses.size());
        // Test part -- elseSeq
        assertNull(caseLang.elseSeq);
    }

    @Test
    public void testCaseAltElseExpr() {
        //                                      1         2         3         4         5
        //                            0123456789012345678901234567890123456789012345678901234
        Parser p = new Parser("case 0 of 0 then true of 1 then false else nothing end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(CaseLang.class, sox);
        CaseLang caseLang = (CaseLang) sox;
        assertSourceSpan(caseLang, 0, 54);
        assertSourceSpan(caseLang.arg, 5, 6);
        // Test format
        String expectedFormat = """
            case 0
                of 0 then
                    true
                of 1 then
                    false
                else
                    nothing
            end""";
        String actualFormat = caseLang.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test part -- caseClause
        assertSourceSpan(caseLang.caseClause, 7, 21);
        assertInstanceOf(IntAsPat.class, caseLang.caseClause.pat);
        assertSourceSpan(caseLang.caseClause.pat, 10, 11);
        // Test part -- caseClause body
        assertEquals(1, caseLang.caseClause.body.list.size());
        SntcOrExpr bodyExpr = caseLang.caseClause.body.list.get(0);
        assertInstanceOf(BoolAsExpr.class, bodyExpr);
        assertSourceSpan(bodyExpr, 17, 21);
        // Test part -- altCaseClauses
        assertEquals(1, caseLang.altCaseClauses.size());
        CaseClause altCaseClause = caseLang.altCaseClauses.get(0);
        assertSourceSpan(altCaseClause, 22, 37);
        assertInstanceOf(IntAsPat.class, altCaseClause.pat);
        assertSourceSpan(altCaseClause.pat, 25, 26);
        // Test part -- elseSeq
        assertNotNull(caseLang.elseSeq);
        assertEquals(1, caseLang.elseSeq.list.size());
        SntcOrExpr elseExpr = caseLang.elseSeq.list.get(0);
        assertInstanceOf(NothingAsExpr.class, elseExpr);
        assertSourceSpan(elseExpr, 43, 50);

    }

    @Test
    public void testCaseElse() {
        //                                      1         2         3
        //                            0123456789012345678901234567890123456
        Parser p = new Parser("case 0 of 0 then true else false end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(CaseLang.class, sox);
        CaseLang caseLang = (CaseLang) sox;
        assertSourceSpan(caseLang, 0, 36);
        assertSourceSpan(caseLang.arg, 5, 6);
        // Test format
        String expectedFormat = """
            case 0
                of 0 then
                    true
                else
                    false
            end""";
        String actualFormat = caseLang.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test part -- caseClause
        assertSourceSpan(caseLang.caseClause, 7, 21);
        assertInstanceOf(IntAsPat.class, caseLang.caseClause.pat);
        assertSourceSpan(caseLang.caseClause.pat, 10, 11);
        // Test part -- caseClause body
        assertEquals(1, caseLang.caseClause.body.list.size());
        SntcOrExpr bodyExpr = caseLang.caseClause.body.list.get(0);
        assertInstanceOf(BoolAsExpr.class, bodyExpr);
        assertSourceSpan(bodyExpr, 17, 21);
        // Test part -- altCaseClauses
        assertEquals(0, caseLang.altCaseClauses.size());
        // Test part -- elseSeq
        assertNotNull(caseLang.elseSeq);
        assertEquals(1, caseLang.elseSeq.list.size());
        SntcOrExpr elseExpr = caseLang.elseSeq.list.get(0);
        assertInstanceOf(BoolAsExpr.class, elseExpr);
        assertSourceSpan(elseExpr, 27, 32);
    }

    @Test
    public void testFormat() {
        //                                      1         2         3         4         5
        //                            012345678901234567890123456789012345678901234567890123
        Parser p = new Parser("case 0 of 0 then a b c of 1 then d e f else g h i end");
        SntcOrExpr sox = p.parse();
        assertInstanceOf(CaseLang.class, sox);
        CaseLang caseLang = (CaseLang) sox;
        assertSourceSpan(caseLang, 0, 53);
        String expectedFormat = """
            case 0
                of 0 then
                    a
                    b
                    c
                of 1 then
                    d
                    e
                    f
                else
                    g
                    h
                    i
            end""";
        String actualFormat = LangFormatter.SINGLETON.format(caseLang);
        assertEquals(expectedFormat, actualFormat);
    }

}
