/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torqlang License v1.0 along with this program.
 * If not, see <http://torqlang.github.io/licensing/torqlang-license-v1_0>.
 */

package org.torqlang.core.lang;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.torqlang.core.lang.CommonTools.assertSourceSpan;

public class TestParserIfLang {

    @Test
    public void testFormat() {
        //                                      1         2          3        4         5
        //                            0123456789012345678901234567890123456789012345678901234567
        Parser p = new Parser("if true then 1 2 3 elseif false then 4 5 6 else 7 8 9 end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof IfLang);
        IfLang ifLang = (IfLang) sox;
        assertSourceSpan(ifLang, 0, 57);
        String expectedFormat = """
            if true then
                1
                2
                3
            elseif false then
                4
                5
                6
            else
                7
                8
                9
            end""";
        String actualFormat = LangFormatter.SINGLETON.format(ifLang);
        assertEquals(expectedFormat, actualFormat);
    }

    @Test
    public void testIf() {
        //                                      1         2
        //                            0123456789012345678901234
        Parser p = new Parser("if true then nothing end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof IfLang);
        IfLang ifLang = (IfLang) sox;
        assertSourceSpan(ifLang, 0, 24);
        // Test format
        String expectedFormat = """
            if true then
                nothing
            end""";
        String actualFormat = ifLang.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test part -- ifClause
        assertSourceSpan(ifLang.ifClause, 0, 20);
        assertTrue(ifLang.ifClause.condition instanceof BoolAsExpr);
        assertSourceSpan(ifLang.ifClause.condition, 3, 7);
        // Test part -- ifClause body
        assertEquals(1, ifLang.ifClause.body.list.size());
        SntcOrExpr bodyExpr = ifLang.ifClause.body.list.get(0);
        assertTrue(bodyExpr instanceof NothingAsExpr);
        assertSourceSpan(bodyExpr, 13, 20);
        // Test part -- altIfClauses
        assertEquals(0, ifLang.altIfClauses.size());
        // Test part -- elseSeq
        assertNull(ifLang.elseSeq);
    }

    @Test
    public void testIfAltElse() {
        //                                      1         2         3         4
        //                            0123456789012345678901234567890123456789012345
        Parser p = new Parser("if true then 0 elseif false then 1 else 2 end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof IfLang);
        IfLang ifLang = (IfLang) sox;
        assertSourceSpan(ifLang, 0, 45);
        // Test format
        String expectedFormat = """
            if true then
                0
            elseif false then
                1
            else
                2
            end""";
        String actualFormat = ifLang.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test part -- ifClause
        assertSourceSpan(ifLang.ifClause, 0, 14);
        assertTrue(ifLang.ifClause.condition instanceof BoolAsExpr);
        assertSourceSpan(ifLang.ifClause.condition, 3, 7);
        // Test part -- ifClause body
        assertEquals(1, ifLang.ifClause.body.list.size());
        SntcOrExpr bodyExpr = ifLang.ifClause.body.list.get(0);
        assertTrue(bodyExpr instanceof IntAsExpr);
        assertSourceSpan(bodyExpr, 13, 14);
        // Test part -- altIfClauses
        assertEquals(1, ifLang.altIfClauses.size());
        IfClause altIfClause = ifLang.altIfClauses.get(0);
        assertSourceSpan(altIfClause, 15, 34);
        assertTrue(altIfClause.condition instanceof BoolAsExpr);
        assertSourceSpan(altIfClause.condition, 22, 27);
        // Test part -- elseSeq
        assertNotNull(ifLang.elseSeq);
        assertEquals(1, ifLang.elseSeq.list.size());
        SntcOrExpr elseExpr = ifLang.elseSeq.list.get(0);
        assertTrue(elseExpr instanceof IntAsExpr);
        assertSourceSpan(elseExpr, 40, 41);
    }

    @Test
    public void testIfElse() {
        //                                      1         2         3
        //                            012345678901234567890123456789012
        Parser p = new Parser("if true then true else false end");
        SntcOrExpr sox = p.parse();
        assertTrue(sox instanceof IfLang);
        IfLang ifLang = (IfLang) sox;
        assertSourceSpan(ifLang, 0, 32);
        // Test format
        String expectedFormat = """
            if true then
                true
            else
                false
            end""";
        String actualFormat = ifLang.toString();
        assertEquals(expectedFormat, actualFormat);
        // Test part -- ifClause
        assertSourceSpan(ifLang.ifClause, 0, 17);
        assertTrue(ifLang.ifClause.condition instanceof BoolAsExpr);
        assertSourceSpan(ifLang.ifClause.condition, 3, 7);
        // Test part -- ifClause body
        assertEquals(1, ifLang.ifClause.body.list.size());
        SntcOrExpr bodyExpr = ifLang.ifClause.body.list.get(0);
        assertTrue(bodyExpr instanceof BoolAsExpr);
        assertSourceSpan(bodyExpr, 13, 17);
        // Test part -- altIfClauses
        assertEquals(0, ifLang.altIfClauses.size());
        // Test part -- elseSeq
        assertNotNull(ifLang.elseSeq);
        assertEquals(1, ifLang.elseSeq.list.size());
        SntcOrExpr elseExpr = ifLang.elseSeq.list.get(0);
        assertTrue(elseExpr instanceof BoolAsExpr);
        assertSourceSpan(elseExpr, 23, 28);
    }

}
